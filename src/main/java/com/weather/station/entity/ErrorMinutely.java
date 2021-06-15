package com.weather.station.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "error_min", schema = "weather")
public class ErrorMinutely {

    @Id
    private LocalDateTime dt;

    @ManyToOne
    private SourceType source;

    @ManyToOne
    private ErrorType error;

    public String errorString(){
        return "Ha habido un error en "+this.source.getSource()+" del tipo "+this.error.getError()+" a la hora "+dt;
    }

}
