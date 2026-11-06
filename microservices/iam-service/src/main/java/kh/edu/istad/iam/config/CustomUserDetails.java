package kh.edu.istad.iam.config;

import kh.edu.istad.iam.domain.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getPermissions()
                .forEach(
                        permission -> {
                            authorities.add(
                                    new SimpleGrantedAuthority(permission.getPermission())
                            );
                        }
                );

        user.getRoles()
                .forEach(
                        role -> {
                            authorities.add(
                                    new SimpleGrantedAuthority("ROLE_" + role.getRole())
                            );
                        }
                );
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public boolean equal(Object obj){
        if(obj instanceof User that){
            return this.user.getUsername().equals(that.getUsername());
        }
        return false;
    }
}
