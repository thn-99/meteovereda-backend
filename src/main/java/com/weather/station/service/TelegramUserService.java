package com.weather.station.service;

import java.util.List;

import com.weather.station.entity.Hour;
import com.weather.station.entity.TelegramUser;

/**
 * @author cleverpy
 *
 */
public interface TelegramUserService {

  public TelegramUser getTelegramUser(Long id);
  public List <TelegramUser> getTelegramUsers();
  public TelegramUser newTelegramUser(TelegramUser telegramUser);
  public List<TelegramUser> newTelegramUsers(List<TelegramUser> telegramUsers);
  public List<TelegramUser> getByHour(Hour hour);
  public List<TelegramUser> getAdmins();
  public void deleteTelegramUser(Long id);
  public TelegramUser updateTelegramUser(TelegramUser telegramUser);
  public Long count();

}
