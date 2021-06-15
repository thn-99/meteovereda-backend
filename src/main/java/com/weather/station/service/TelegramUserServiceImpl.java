package com.weather.station.service;

import java.util.List;

import com.weather.station.entity.Hour;
import com.weather.station.entity.TelegramUser;
import com.weather.station.repository.TelergamUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cleverpy
 *
 */
@Slf4j
@Service
public class TelegramUserServiceImpl implements TelegramUserService {

  private final TelergamUserRepository telegramUserRepository;

  public TelegramUserServiceImpl(TelergamUserRepository telegramUserRepository) {
    this.telegramUserRepository = telegramUserRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public TelegramUser getTelegramUser(Long id) {
    try {
      return telegramUserRepository.findById(id).get();

    } catch (Exception e) {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public List<TelegramUser> getTelegramUsers() {
    return telegramUserRepository.findAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public TelegramUser newTelegramUser(TelegramUser telegramUser) {
    telegramUser = telegramUserRepository.save(telegramUser);
    return telegramUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public List<TelegramUser> newTelegramUsers(List<TelegramUser> telegramUsers) {
    for (TelegramUser telegramUser : telegramUsers) {
      telegramUserRepository.save(telegramUser);
    }
    return telegramUsers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void deleteTelegramUser(Long id) {
    telegramUserRepository.deleteById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public TelegramUser updateTelegramUser(TelegramUser telegramUser) {
    return telegramUserRepository.save(telegramUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public Long count() {
    return telegramUserRepository.count();
  }

@Override
public List<TelegramUser> getAdmins() {
	return this.telegramUserRepository.findByAdminTrue();
}

@Override
public List<TelegramUser> getByHour(Hour hour) {
  return this.telegramUserRepository.findByWeatherSuscribeHour(hour);
}
}
