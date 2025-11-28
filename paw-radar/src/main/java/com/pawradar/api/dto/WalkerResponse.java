package com.pawradar.api.dto;

import com.pawradar.api.domain.DogSize;
import com.pawradar.api.domain.Walker;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WalkerResponse {
    private String name;
    private DogSize dogSize;
    private double distanceMeters; // 나와의 거리
    private int matchScore;        // 매칭 점수 (높을수록 추천)
    private Double latitude;
    private Double longitude;

    public WalkerResponse(Walker walker, double distance, int score) {
        this.name = walker.getName();
        this.dogSize = walker.getDogSize();
        this.distanceMeters = Math.round(distance); // 소수점 반올림
        this.matchScore = score;
        this.setLatitude(walker.getLatitude());
        this.setLongitude(walker.getLongitude());
    }
    
	public int getMatchScore() {
		return this.matchScore;
	}

	public DogSize getDogSize() {
		return dogSize;
	}

	public void setDogSize(DogSize dogSize) {
		this.dogSize = dogSize;
	}

	public double getDistanceMeters() {
		return distanceMeters;
	}

	public void setDistanceMeters(double distanceMeters) {
		this.distanceMeters = distanceMeters;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMatchScore(int matchScore) {
		this.matchScore = matchScore;
	}

	public String getName() {
		return this.name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}