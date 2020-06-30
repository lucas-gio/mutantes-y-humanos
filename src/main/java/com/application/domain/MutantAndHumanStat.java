package com.application.domain;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase que representa una estadística de cantidad de humanos, mutantes, y su relación.
 */
public class MutantAndHumanStat {
	private long countMutantDna;
	private long countHumanDna;
	private BigDecimal ratio;

	public MutantAndHumanStat(long countMutantDna, long countHumanDna, BigDecimal ratio) {
		setCountMutantDna(countMutantDna);
		setCountHumanDna(countHumanDna);
		setRatio(ratio);
	}

	public long getCountMutantDna() {
		return this.countMutantDna;
	}

	public void setCountMutantDna(long countMutantDna) {
		this.countMutantDna = countMutantDna;
	}

	public long getCountHumanDna() {
		return this.countHumanDna;
	}

	public void setCountHumanDna(long countHumanDna) {
		this.countHumanDna = countHumanDna;
	}

	public BigDecimal getRatio() {
		return this.ratio;
	}

	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}

	public String toJson(){
		Map result = new LinkedHashMap();
		result.put("count_mutant_dna", getCountMutantDna());
		result.put("count_human_dna", getCountHumanDna());
		result.put("ratio", getRatio());

		return new Gson().toJson(result);
	}
}
