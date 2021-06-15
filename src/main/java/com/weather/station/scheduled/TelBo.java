package com.weather.station.scheduled;

import java.util.ArrayList;
import java.util.List;

import com.weather.station.entity.Hour;
import com.weather.station.entity.StationWeatherMinutely;
import com.weather.station.entity.TelegramUser;
import com.weather.station.entity.TemporaryJson;
import com.weather.station.service.HourService;
import com.weather.station.service.StationWeatherMinutelyService;
import com.weather.station.service.TelegramUserService;
import com.weather.station.service.TemporaryJsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

@Component
@EnableScheduling
public class TelBo extends TelegramLongPollingBot {

    @Autowired
    private StationWeatherMinutelyService stationWeatherMinutelyService;

    @Autowired
    private TelegramUserService telegramUserService;

    @Autowired
    private HourService hourService;

    @Autowired
    private TemporaryJsonService temporaryJsonService;

    @Value("${telegram.admin}")
    private Long telegramAdminId;

    @Value("${telegram.bottoken}")
    private String botToken;

    @Value("${telegram.botusername}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        if (!update.hasCallbackQuery()) {
            Message message = update.getMessage();
            String text = message.getText();

            if (text.charAt(0) == "/".charAt(0)) {
                String[] texts = text.split(" ");
                switch (texts[0]) {
                    case "/start":
                        startCommand(message.getChatId().toString());
                        break;
                    case "/ahora":
                        currentWeather(message.getChatId().toString());
                        break;
                    case "/lista":
                        listHandler(message.getChatId().toString());
                        break;
                    case "/suscribir":
                        suscriptionHandler(message.getChatId().toString());
                        break;
                    case "/cancelarsub":
                        suscriptionCancelHandler(message.getChatId());
                        break;
                    case "/prediccion":
                        predictionHandler(message.getChatId().toString());
                        break;
                    case "/id":
                        idHandler(message.getChatId().toString());
                        break;
                    case "/addadmin":
                        addAdminHandler(message);
                        break;
                    case "/canceladmin":
                        cancelAdminHandler(message.getChatId().toString());
                        break;
                    default:
                        deliverMessage(message.getChatId().toString(),
                                "Comando no encontrado, mira la lista de comandos para ver las interacciones posible");
                        listHandler(message.getChatId().toString());
                        break;
                }
                System.out.println("es un comando");
            } else if (message.isReply()) {
                Message replyMessage = message.getReplyToMessage();
                switch (replyMessage.getText()) {
                    case "Introduce la hora (0-23) a la que quieres recibir la prediccion":
                        suscriptionReplyHandler(message);
                        break;

                    default:
                        break;
                }
                System.out.println(replyMessage.hasViaBot());
                System.out.println(replyMessage.getText());
            }

        } else {
            System.out.println("Tiene callback" + update);
        }
    }

    public void sendMessageToAllAdmin(String message){
        for(TelegramUser user: this.telegramUserService.getAdmins()){
            System.out.println("");
            deliverMessage(user.getUserId().toString(), message);
        }
    }

    private void cancelAdminHandler(String chatId) {
        Long chatLong = Long.parseLong(chatId);
        TelegramUser user = this.telegramUserService.getTelegramUser(chatLong);
        if (user == null) {
            deliverMessage(chatId, "No estas registrado ");
            return;
        }
        if (!user.getAdmin()) {
            deliverMessage(chatId, "No eres admin");
            return;
        }
        user.setAdmin(false);

        this.telegramUserService.updateTelegramUser(user);
        deliverMessage(chatId, "Ya no recibiras notificaciones de error");

    }

    private void addAdminHandler(Message message) {

        String[] texts = message.getText().split(" ");
        String chatId = message.getChatId().toString();
        if (!telegramAdminId.toString().equals(chatId)) {
            deliverMessage(chatId, "No eres admin");
        }

        if (texts.length < 2) {
            deliverMessage(chatId, "Envia el id de la persona a la que quieres hacer admin");
            return;
        }
        Long id = null;
        try {
            id = Long.parseLong(texts[1]);
        } catch (Exception e) {
            deliverMessage(chatId, "Envia un id correcto");
            return;
        }
        TelegramUser newAdmin = this.telegramUserService.getTelegramUser(id);
        if (newAdmin == null) {
            newAdmin = new TelegramUser(id, true, false, null);
        } else {
            newAdmin.setAdmin(true);
        }
        this.telegramUserService.updateTelegramUser(newAdmin);

        deliverMessage(chatId, "Añadido correctamente");

    }

    private void idHandler(String chatId) {
        deliverMessage(chatId, "Tu id es: " + chatId);
    }

    private void predictionHandler(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String textToSend = "";
        TemporaryJson tempOpen = this.temporaryJsonService.getLast();

        JsonNode tempOpenJsonNode = new JsonNode(tempOpen.getJsonString());

        JSONObject tempOpenObject = tempOpenJsonNode.getObject();

        JSONObject dailyToday = tempOpenObject.getJSONArray("daily").getJSONObject(0);
        JSONObject dailyTomorrow = tempOpenObject.getJSONArray("daily").getJSONObject(1);

        float todayMaxTemp = dailyToday.getJSONObject("temp").getFloat("min");
        float todayMinTemp = dailyToday.getJSONObject("temp").getFloat("max");
        float todayPop = dailyToday.getFloat("pop");
        float todayRain;

        try {
            todayRain = dailyToday.getFloat("rain");
        } catch (JSONException e) {
            todayRain = 0;
        }
        float todayWindSpeed = dailyToday.getFloat("wind_speed");
        float todayWindDeg = dailyToday.getFloat("wind_deg");
        float todayClouds = dailyToday.getFloat("clouds");
        float todayUvi = dailyToday.getFloat("uvi");

        float tomorrowMaxTemp = dailyTomorrow.getJSONObject("temp").getFloat("min");
        float tomorrowMinTemp = dailyTomorrow.getJSONObject("temp").getFloat("max");
        float tomorrowPop = dailyTomorrow.getFloat("pop");
        float tomorrowRain;
        try {
            tomorrowRain = dailyTomorrow.getFloat("rain");
        } catch (JSONException e) {
            tomorrowRain = 0;
        }

        float tomorrowWindSpeed = dailyTomorrow.getFloat("wind_speed");
        float tomorrowWindDeg = dailyTomorrow.getFloat("wind_deg");
        float tomorrowClouds = dailyTomorrow.getFloat("clouds");
        float tomorrowUvi = dailyTomorrow.getFloat("uvi");

        String textToSendToday = "Hoy:\n" + "Temperatura Máxima: " + todayMaxTemp + "ºC" + "\n" + "Temperatura Mínima: "
                + todayMinTemp + "ºC" + "\n" + "Probabilidad de lluvia: " + todayPop * 100 + "%";
        if (todayPop > 0 && todayRain != 0) {
            textToSendToday += "  precipitación: " + todayRain + " ml\n";
        }

        textToSendToday += "Nubes: " + todayClouds + "%" + "\n" + "Viento: " + todayWindSpeed + " km/h"
                + "  Dirección: " + todayWindDeg + "º\n" + "UVI: " + todayUvi + "\n\n";

        String textToSendTomorrow = "Mañana:\n" + "Temperatura Máxima: " + tomorrowMaxTemp + "ºC" + "\n"
                + "Temperatura Mínima: " + tomorrowMinTemp + "ºC" + "\n" + "Probabilidad de lluvia: "
                + tomorrowPop * 100 + "%";
        if (tomorrowPop > 0 && tomorrowRain != 0) {
            textToSendTomorrow += "  precipitación: " + tomorrowRain + " ml\n";
        }

        textToSendTomorrow += "Nubes: " + tomorrowClouds + "%" + "\n" + "Viento: " + tomorrowWindSpeed + " km/h"
                + "  Dirección: " + tomorrowWindDeg + "º\n" + "UVI:" + tomorrowUvi;

        textToSend += textToSendToday + textToSendTomorrow;
        sendMessage.setText(textToSend);
        deliverMessage(sendMessage);
    }

    private void startCommand(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String textToSend = "";
        textToSend += "Bienvenido al bot de Telegram de Ies La Vereda.\n"
                + "Este bot te permite recibir notificaciones referentes al tiempo enviadas"
                + "por el centro, suscribirte para recibir prediccion del tiempo cada día, y ver"
                + "la información de la estación meteorológica.\n";
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void listHandler(String chatId) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        String textToSend = "";
        textToSend += "A continuación tienes la lista actual de comandos disponibles:\n";
        textToSend += "/ahora Para ver las condiciones meteorológicas de la estación de la Vereda \n";
        textToSend += "/prediccion Para ver la prediccion para hoy\n";
        textToSend += "/suscribir Para recibir todos los dias la prediccion a la hora introducida (hora entre 0-24)\n";
        textToSend += "/cancelarsub Para dejar de recibir predicciones\n";
        textToSend += "/lista  Lista comandos\n";
        textToSend += "/canceladmin Para dejar de recibir notificaciones sobre errores\n";


        s.setText(textToSend);
        try {
            execute(s);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void dailyPrediction(Hour hour){
        List<TelegramUser> telegramUsers = this.telegramUserService.getByHour(hour);

        for (TelegramUser telegramUser : telegramUsers) {
            predictionHandler(telegramUser.getUserId().toString());
        }

    }

    public void centerNotificationsHandler(String chatId) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s = new SendMessage();
        s.setChatId(chatId);

        String textToSend = "";
        textToSend = "Deseas recibir notificaciones enviadas por el centro referentes al tiempo";
        // ReplyKeyboard forceReply = new ForceReplyKeyboard(true);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();

        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton("Si");
        yesButton.setCallbackData("si");

        InlineKeyboardButton noButton = new InlineKeyboardButton("No");
        noButton.setCallbackData("no");

        row.add(yesButton);
        row.add(noButton);
        keyboard.add(row);

        ReplyKeyboard awnsers = InlineKeyboardMarkup.builder().keyboard(keyboard).build();
        s.setText(textToSend);
        s.setReplyMarkup(awnsers);

        try {
            execute(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void currentWeather(String chatId) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        StationWeatherMinutely station = this.stationWeatherMinutelyService.getLast();
        String textToSend = "";
        textToSend += "Temperatura: " + station.getTemp() + " ºC " + "\n";
        textToSend += "Viento: " + station.getWindSpeed() + " km/h " + "  -  " + station.getWindDirection() + "\n";
        textToSend += "Humedad: " + station.getHumidity() + " % " + "\n";
        textToSend += "Barómetro: " + station.getBarometer() + " Hpa " + "  -  " + station.getBarometerTrend() + "\n";
        s.setText(textToSend);

        try {
            execute(s);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void suscriptionHandler(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String textToSend = "";
        textToSend = "Introduce la hora (0-23) a la que quieres recibir la prediccion";
        ReplyKeyboard forceReply = new ForceReplyKeyboard(true);
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(forceReply);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void suscriptionReplyHandler(Message message) {
        String chatId = message.getChatId().toString();
        String hourString = message.getText();
        hourString = hourString.trim();
        int hour;
        try {
            hour = Integer.parseInt(hourString);
        } catch (Exception e) {
            deliverMessage(chatId, "Hora invalida, debes enviar un número(0-23)");
            return;
        }
        if (hour < 0 || hour > 23) {
            deliverMessage(chatId, "La hora debe ser entre 0 y 23");
            return;
        }

        Hour dbHour = this.hourService.getByHour(hour);
        TelegramUser user = this.telegramUserService.getTelegramUser(message.getChatId());

        if (user == null) {
            user = new TelegramUser(message.getChatId(), false, false, null);
        }

        if (dbHour != null) {
            user.setWeatherSuscribeHour(dbHour);
        } else {
            deliverMessage(chatId, "No se ha encontrado la hora");
            return;
        }

        this.telegramUserService.updateTelegramUser(user);

        deliverMessage(chatId, "Se ha registrado su preferencia correctamente");
    }

    public void suscriptionCancelHandler(Long chatId) {
        this.telegramUserService.deleteTelegramUser(chatId);
        deliverMessage(chatId.toString(),
                "Te has desuscribido de las notificaciones diarias del tiempo de forma exitosa");
    }

    public void deliverMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deliverMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

}
