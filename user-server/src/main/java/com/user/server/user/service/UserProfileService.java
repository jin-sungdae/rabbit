package com.user.server.user.service;

import com.user.server.user.dto.RequestUser;
import com.user.server.user.dto.RequestUserProfile;
import com.user.server.user.dto.ResponseUserProfile;
import com.user.server.user.entity.User;
import com.user.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseUserProfile getUserProfile(String uid) {
        try {
            User user = userRepository.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            ResponseUserProfile responseUserProfile = ResponseUserProfile.toDto(user);

            return responseUserProfile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Transactional
    public void updateUserProfile(String uid, RequestUserProfile dto) {

        try {
            User user = userRepository.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            user.setUserName(dto.getUserName());
            user.setEmail(dto.getEmail());
            user.setPhoneNo(dto.getPhoneNo());
            user.setBirthday(dto.getBirthday());
            user.setZipcode(dto.getZipcode());
            user.setAddress1(dto.getAddress1());
            user.setAddress2(dto.getAddress2());
            user.setMailing(dto.isMailing());
            user.setSms(dto.isSms());

        } catch (Exception e) {
            e.printStackTrace();
        }




        log.info("updateUserProfile");
    }

}
