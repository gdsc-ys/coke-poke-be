package com.inshining.poke.domain.service;

import com.inshining.poke.config.security.TokenProvider;
import com.inshining.poke.domain.dto.SignInRequest;
import com.inshining.poke.domain.dto.SignInResponse;
import com.inshining.poke.domain.dto.SignUpRequest;
import com.inshining.poke.domain.dto.SignUpResponse;
import com.inshining.poke.domain.entity.user.User;
import com.inshining.poke.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignUpResponse registerUser(SignUpRequest request) {
        User user = userRepository.save(User.from(request, encoder));
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        return SignUpResponse.from(user);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request){
        User user = userRepository.findByUsername(request.username())
                .filter(finedUser -> encoder.matches(request.password(), finedUser.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다."));
        String accessToken = tokenProvider.createAccessToken(String.format("%s", user.getId()));
        return new SignInResponse(user.getName(), accessToken);
    }

}
