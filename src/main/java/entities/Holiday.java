package entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;


//create table holiday (id bigint auto_increment,
//                      from_date date not null,
//                      to_date date not null,
//                      description varchar(100),
//primary key (id));

public class Holiday implements Serializable {

    private BigDecimal id;
    @SerializedName("from_date")
    private String fromDate;
    @SerializedName("to_date")
    private String toDate;
    private String description;

    public Holiday(String fromDate, String toDate, String description) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

