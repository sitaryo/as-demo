package com.sendroids.as.service;

import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.repo.UserRepo;
import com.sendroids.usersyncas.ASUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements ASUserService<UserEntity> {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void save(UserEntity userEntity) {
        userRepo.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findUserByClientIdAndUsername(String clientId, String username) {
        return userRepo.findByClientIdAndUsername(clientId, username);
    }

    @Override
    public Optional<UserEntity> findUserByClientIdAndUnionId(String client, String unionId) {
        return userRepo.findByUnionIdAndClientId(unionId, client);
    }

    @Override
    public void update(UserEntity asUser, UserEntity clientUser) {
        asUser.setPassword(clientUser.getPassword());
        asUser.setAuthorities(clientUser.getAuthorities());

        asUser.setEnabled(clientUser.isEnabled());
        asUser.setCredentialsNonExpired(clientUser.isCredentialsNonExpired());
        asUser.setAccountNonExpired(clientUser.isAccountNonExpired());
        asUser.setAccountNonLocked(clientUser.isAccountNonLocked());
        BeanUtils.copyProperties(
                clientUser.getUserProfile(),
                asUser.getUserProfile(),
                "id",
                "version"
        );
    }

    public void delete(UserEntity user) {
        userRepo.delete(user);
    }
}
