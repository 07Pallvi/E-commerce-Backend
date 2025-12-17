package com.ecommerce.app.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;	
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public abstract class AuditModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	@CreationTimestamp
	protected LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, updatable = true)
	@LastModifiedDate
	@UpdateTimestamp
	protected LocalDateTime updatedAt;

	@Column(name = "created_by", nullable = false, updatable = true)
    @JsonIgnore
    @CreatedBy
    public String createdBy;

	@Column(name = "updated_by", nullable = false, updatable = true)
    @JsonIgnore
    @LastModifiedBy
    public String updatedBy;

	public LocalDateTime getCreatedAtIST() {
		return convertToIST(this.createdAt);
	}
	
	public LocalDateTime getUpdatedAtIST() {
		return convertToIST(this.updatedAt);
	}
	
	private LocalDateTime convertToIST(LocalDateTime date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
		calendar.add(Calendar.HOUR_OF_DAY, 5);
		calendar.add(Calendar.MINUTE, 30);
		return date.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public String getCreatedAtISTFormatted() {
		if (createdAt == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(Date.from(getCreatedAtIST().atZone(ZoneId.systemDefault()).toInstant()));
	}
	
	public String getUpdatedAtISTFormatted() {
		if (updatedAt == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()));
	}
}