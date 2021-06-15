package com.weather.station.service;

import java.time.LocalDateTime;

import com.weather.station.entity.ErrorMinutely;
import com.weather.station.entity.ErrorType;
import com.weather.station.entity.SourceType;
import com.weather.station.repository.ErrorMinutelyRepository;
import com.weather.station.repository.ErrorTypeRepository;
import com.weather.station.repository.SourceTypeRepository;
import com.weather.station.scheduled.TelBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ErrorMinutelyService {

    private ErrorMinutelyRepository errorMinutelyRepository;

    private SourceTypeRepository sourceTypeRepository;

    private ErrorTypeRepository errorTypeRepository;

    @Value("${admin.mail}")
    private String adminMail;

    @Autowired
    private TelBo telbo;

    @Autowired
    EmailService emailService;

    public ErrorMinutelyService(ErrorMinutelyRepository errorMinutelyRepository,SourceTypeRepository sourceTypeRepository,ErrorTypeRepository errorTypeRepository){
        this.errorMinutelyRepository=errorMinutelyRepository;
        this.sourceTypeRepository=sourceTypeRepository;
        this.errorTypeRepository=errorTypeRepository;
    }

    public ErrorMinutely newError(LocalDateTime dt,long sourceId,long errorTypeId){
        SourceType sourceType = this.sourceTypeRepository.findById(sourceId).get();
        ErrorType errorType = this.errorTypeRepository.findById(errorTypeId).get();
        ErrorMinutely errorMinutely = new ErrorMinutely(dt,sourceType,errorType );
        telbo.sendMessageToAllAdmin(errorMinutely.errorString());
        this.emailService.sendSimpleMessage(adminMail, "error weather service", errorMinutely.errorString());
        return this.errorMinutelyRepository.save(errorMinutely);
    }

    public ErrorMinutely getLast(){
        try {
            return this.errorMinutelyRepository.findTopByOrderByDtDesc();

        } catch (Exception e) {
            return null;
        }
    }
    
}
