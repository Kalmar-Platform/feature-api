package com.visma.kalmar.api.config;

import com.visma.kalmar.api.VismaConnectUserGatewayAdapter;
import com.visma.kalmar.api.adapters.language.LanguageGatewayAdapter;
import com.visma.kalmar.api.adapters.user.UserGatewayAdapter;
import com.visma.kalmar.api.language.LanguageGateway;
import com.visma.kalmar.api.user.CreateUserInputPort;
import com.visma.kalmar.api.user.CreateUserUseCase;
import com.visma.kalmar.api.user.UpdateUserInputPort;
import com.visma.kalmar.api.user.UpdateUserUseCase;
import com.visma.kalmar.api.user.DeleteUserInputPort;
import com.visma.kalmar.api.user.DeleteUserUseCase;
import com.visma.kalmar.api.user.UserGateway;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;
import com.visma.useraccess.kalmar.api.language.LanguageRepository;
import com.visma.useraccess.kalmar.api.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

  @Bean
  public UserGateway userGateway(UserRepository userAccessUserRepository) {
    return new UserGatewayAdapter(userAccessUserRepository);
  }

  @Bean
  public LanguageGateway languageGateway(
      LanguageRepository userAccessLanguageRepository) {
    return new LanguageGatewayAdapter(userAccessLanguageRepository);
  }

  @Bean
  public VismaConnectUserGateway vismaConnectUserGateway(
      org.springframework.web.reactive.function.client.WebClient webClient,
      LanguageGateway languageGateway) {
    return new VismaConnectUserGatewayAdapter(webClient, languageGateway);
  }

  @Bean
  public CreateUserInputPort createUserInputPort(
      UserGateway userGateway,
      LanguageGateway languageGateway,
      VismaConnectUserGateway vismaConnectUserGateway) {
    return new CreateUserUseCase(userGateway, languageGateway, vismaConnectUserGateway);
  }

  @Bean
  public UpdateUserInputPort updateUserInputPort(
      UserGateway userGateway,
      LanguageGateway languageGateway,
      VismaConnectUserGateway vismaConnectUserGateway) {
    return new UpdateUserUseCase(userGateway, languageGateway, vismaConnectUserGateway);
  }

  @Bean
  public DeleteUserInputPort deleteUserInputPort(
      UserGateway userGateway, VismaConnectUserGateway vismaConnectUserGateway) {
    return new DeleteUserUseCase(userGateway, vismaConnectUserGateway);
  }
}
