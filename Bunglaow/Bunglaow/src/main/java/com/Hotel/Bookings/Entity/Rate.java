package com.Hotel.Bookings.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;




@Entity
@Table(name="rate")
public class Rate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@NotNull(message = "Stay date from is required")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Column(name= "stayDateFrom")
	private LocalDate stayDateFrom;

	@NotNull(message = "Stay date to is required")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Column(name="stayDateTo")
	private LocalDate stayDateTo;

	@NotNull(message = "Nights is required")
	@Positive(message = "Nights should be a positive number")
	@Column(name= "nights")
	private Integer nights;

	@NotNull(message = "Value is required")
	@Positive(message = "Value should be a positive number")
    @Column(name= "value")
	private Double value;

	@NotNull(message = "Bungalow ID is required")
	@Positive(message = "Bungalow ID should be a positive number")
	@Column(name="bungalowId")
	private Long bungalowId;

    
	@Column(name="closedDate")
	@Nullable
	private LocalDateTime closedDate;

	public Rate() {
		super();
	}

	public Rate(Rate rate) {
		super();
		this.stayDateFrom = rate.stayDateFrom;
		this.stayDateTo = rate.stayDateTo;
		this.nights = rate.nights;
		this.value = rate.value;
		this.bungalowId = rate.bungalowId;
		this.closedDate = rate.closedDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Rate otherRate = (Rate) obj;
		// Compare fields for equality
		return Objects.equals(stayDateFrom, otherRate.stayDateFrom) &&
				Objects.equals(stayDateTo, otherRate.stayDateTo) &&
				Objects.equals(nights, otherRate.nights) &&
				Objects.equals(value, otherRate.value) &&
				Objects.equals(bungalowId, otherRate.bungalowId);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getStayDateFrom() {
		return stayDateFrom;
	}

	public void setStayDateFrom(LocalDate stayDateFrom) {
		this.stayDateFrom = stayDateFrom;
	}

	public LocalDate getStayDateTo() {
		return stayDateTo;
	}

	public void setStayDateTo(LocalDate stayDateTo) {
		this.stayDateTo = stayDateTo;
	}
	
	public int getNights() {
		return nights;
	}

	public void setNights(Integer nights) {
		this.nights = nights;
	}

	public double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Long getBungalowId() {
		return bungalowId;
	}

	public void setBungalowId(Long bungalowId) {
		this.bungalowId = bungalowId;
	}

	public LocalDateTime getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(LocalDateTime closedDate) {
		this.closedDate = closedDate;
	}

	@Override
	public String toString() {
		return "Rate [id=" + id + ", stayDateFrom=" + stayDateFrom + ", stayDateTo=" + stayDateTo + ", nights=" + nights
				+ ", value=" + value + ", bungalowId=" + bungalowId + ", closedDate=" + closedDate + "]";
	}

}
