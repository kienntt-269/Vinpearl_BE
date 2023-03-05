package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.config.JwtTokenProvider;
import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.model.User;
import dev.kienntt.demo.BE_Vinpearl.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

    @Autowired
    private UserService userService;

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @PostMapping("/register")
    public ResponseMessage create(@RequestBody User user) {
        String md5Password = DigestUtils.md5Hex(user.getPassword()).toUpperCase();
        user.setPassword(md5Password);
        userService.save(user);
        return new ResponseMessage(200, "Tạo tài khoản thành công", null, null);
    }

//    @PostMapping("/login")
//    public ResponseMessage login(@RequestBody User user) throws InvalidKeySpecException, NoSuchAlgorithmException {
//        return new ResponseMessage(200, "Success", jwtTokenProvider.createJwtSignedHMAC(user), null);
//    }

    @PostMapping("/login")
    public ResponseMessage login(@RequestBody User user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User userDb = userService.findByEmail(user.getEmail());
        String md5Password = DigestUtils.md5Hex(user.getPassword()).toUpperCase();
//        System.out.println("Verify: " + userDb.getPassword().equals(md5Password));
        if (!userDb.getEmail().equals(user.getEmail())) {
            return new ResponseMessage(400, "Fail","Tài khoản không tồn tại", null);
        } else if (!userDb.getPassword().equals(md5Password)) {
            return new ResponseMessage(400, "Fail", "Mật khẩu không đúng", null);
        }
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
        userDb.setToken(jwtTokenProvider.createJwtSignedHMAC(userDb));
        return new ResponseMessage(200, "Success", userDb, null);
    }

    @GetMapping("/findAll")
    public ResponseMessage findAll() {
        Iterable<User> listUser = userService.findAll();
        return new ResponseMessage(200, "Success", listUser, null);
    }

    @GetMapping("/detail/{id}")
    public ResponseMessage getUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional.map(user -> new ResponseMessage(200, "Success", user, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @PutMapping("/updateUser")
    public ResponseMessage updateUser(@PathVariable("id") Long userId, @RequestBody User user) {
        user.setId(userId);
        userService.updateUser(user);
        return new ResponseMessage(200, "Success", "", null);
    }

    @GetMapping("/delete/{roomId}")
    public ResponseMessage deleteRoom(@PathVariable Long roomId) {
        userService.deleteUser(roomId);
        return new ResponseMessage(200, "User successfully deleted!", null, null);
    }
}
