package dongguk.capstone.backend.user.service;

import dongguk.capstone.backend.user.dto.request.LoginReqDTO;
import dongguk.capstone.backend.user.dto.request.SignupReqDTO;
import dongguk.capstone.backend.user.dto.response.LoginResDTO;
import dongguk.capstone.backend.user.entity.User;

public interface UserService {
    // 회원가입
    User save(SignupReqDTO signupReqDTO);

    // 로그인
    LoginResDTO login(LoginReqDTO loginReqDTO);
}
