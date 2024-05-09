package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.user.User;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UserDto {

        private Long id;
        private String uuid;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String countryOfResidence;

        private String message;

        public User fromDto() {
            return User.builder()
                    .id(this.id)
                    .uuid(this.uuid)
                    .username(this.username)
                    .password(this.password)
                    .firstName(this.firstName)
                    .lastName(this.lastName)
                    .email(this.email)
                    .countryOfResidence(this.countryOfResidence)
                    .build();
        }
}
