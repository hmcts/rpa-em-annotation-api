package uk.gov.hmcts.reform.em.annotation.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

@Component
public class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String TOKEN_NAME = "tokenName";

    private final IdamDetailsFilterService idamDetailsFilterService;

    private final IdamRepository idamRepository;


    @Autowired
    public JwtGrantedAuthoritiesConverter(IdamDetailsFilterService idamDetailsFilterService,
                                          IdamRepository idamRepository) {
        this.idamDetailsFilterService = idamDetailsFilterService;
        this.idamRepository = idamRepository;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (jwt.containsClaim(TOKEN_NAME) && jwt.getClaim(TOKEN_NAME).equals(ACCESS_TOKEN)) {
            UserDetails userDetails = idamRepository.getUserDetails(jwt.getTokenValue());
            idamDetailsFilterService.saveIdamDetails(userDetails);
        }
        return authorities;
    }


}
