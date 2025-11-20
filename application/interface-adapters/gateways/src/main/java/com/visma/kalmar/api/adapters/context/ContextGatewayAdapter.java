package com.visma.kalmar.api.adapters.context;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.subscription.kalmar.api.context.ContextRepository;
import com.visma.useraccess.kalmar.api.context.UserAccessContextRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class ContextGatewayAdapter implements ContextGateway {

  private final UserAccessContextRepository userAccessContextRepository;
  private final ContextRepository subscriptionContextRepository;

  @Autowired
  public ContextGatewayAdapter(
          UserAccessContextRepository userAccessContextRepository,
      ContextRepository subscriptionContextRepository) {
    this.userAccessContextRepository = userAccessContextRepository;
    this.subscriptionContextRepository = subscriptionContextRepository;
  }

  @Override
  public Context findUserAccessContextById(UUID id) {
    return userAccessContextRepository.findById(id).map(this::toDomainContext).orElse(null);
  }

  @Override
  public Context findSubscriptionContextById(UUID id) {
    return subscriptionContextRepository.findById(id).map(this::toDomainContext).orElse(null);
  }

  @Override
  public void save(Context context) {
    userAccessContextRepository.save(toUserAccessContext(context));
    subscriptionContextRepository.save(toSubscriptionContext(context));
  }

  private Context toDomainContext(
      com.visma.useraccess.kalmar.api.context.Context context) {
    var contextEntity = new Context();
    contextEntity.setIdContext(context.getIdContext());
    contextEntity.setIdContextParent(context.getIdContextParent());
    contextEntity.setIdCountry(context.getIdCountry());
    contextEntity.setName(context.getName());
    contextEntity.setOrganizationNumber(context.getOrganizationNumber());
    return contextEntity;
  }

  private Context toDomainContext(
      com.visma.subscription.kalmar.api.context.Context context) {
    var contextEntity = new Context();
    contextEntity.setIdContext(context.getIdContext());
    contextEntity.setIdContextParent(context.getIdContextParent());
    contextEntity.setIdCountry(context.getIdCountry());
    contextEntity.setName(context.getName());
    contextEntity.setOrganizationNumber(context.getOrganizationNumber());
    return contextEntity;
  }

  private com.visma.useraccess.kalmar.api.context.Context toUserAccessContext(
      Context contextEntity) {
    var context = new com.visma.useraccess.kalmar.api.context.Context();
    context.setIdContext(contextEntity.getIdContext());
    context.setIdContextParent(contextEntity.getIdContextParent());
    context.setIdCountry(contextEntity.getIdCountry());
    context.setName(contextEntity.getName());
    context.setOrganizationNumber(contextEntity.getOrganizationNumber());
    return context;
  }

  private com.visma.subscription.kalmar.api.context.Context toSubscriptionContext(
      Context contextEntity) {
    var context = new com.visma.subscription.kalmar.api.context.Context();
    context.setIdContext(contextEntity.getIdContext());
    context.setIdContextParent(contextEntity.getIdContextParent());
    context.setIdCountry(contextEntity.getIdCountry());
    context.setName(contextEntity.getName());
    context.setOrganizationNumber(contextEntity.getOrganizationNumber());
    return context;
  }
}
