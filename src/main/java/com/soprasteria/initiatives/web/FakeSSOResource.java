package com.soprasteria.initiatives.web;

import com.mongodb.reactivestreams.client.MongoClient;
import com.soprasteria.initiatives.config.ProfileConstants;
import com.soprasteria.initiatives.config.SecurityConstants;
import com.soprasteria.initiatives.domain.Authority;
import com.soprasteria.initiatives.domain.User;
import com.soprasteria.initiatives.repository.AuthorityRepository;
import com.soprasteria.initiatives.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Base64;

/**
 * Resource fake SSO endpoint
 *
 * @author cegiraud
 */
@RestController
@Profile(ProfileConstants.BOUCHON)
public class FakeSSOResource {

    @GetMapping(ApiConstants.FAKE_SSO)
    @ResponseBody
    public String me(@RequestHeader String authorization) {
        String token = StringUtils.substringAfter(authorization, SecurityConstants.BEARER_PREFIX);
        return new String(Base64.getUrlDecoder().decode(token));
    }

    /**
     * Endpoint facilitant la génération de token.
     * Utiliser ce token pour s'authentifier en POST sur le même endpoint avec :
     * - accessToken = <token réccupéré>
     * - ssoProdider = fakesso
     *
     * @param idsso     : l'idsso souhaité.
     * @param firstName : le prénom de l'utilisateur
     * @param lastName  : le nom de l'utiliateur
     * @return l'accessToken
     */
    @ApiOperation(value = "Endpoint facilitant la génération de token",
            notes = "Utiliser le token réccupéré pour s'authentifier en POST avec :\n" +
                    "* accessToken = &lt;le token réccupéré&gt;\n" +
                    "* ssoProdider = fakesso")
    @GetMapping(ApiConstants.TOKENS)
    @ResponseBody
    public String createAccessToken(@RequestParam String idsso, @RequestParam String firstName, @RequestParam String lastName) throws JSONException {
        JSONObject user = new JSONObject();
        user.put("id", idsso);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(user.toString().getBytes());
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, AuthorityRepository authorityRepository, MongoClient mongoClient) {
        return args -> {
            Mono.from(mongoClient.getDatabase("initiatives").drop()).block();
            User user = new User();
            user.setUsername("Charles");
            userRepository.save(user).block();
            Authority authority = authorityRepository.save(new Authority(Authority.DEFAULT_AUTHORITY)).block();
            user.getAuthorities().add(authority);
            userRepository.save(user).block();
        };

    }
}