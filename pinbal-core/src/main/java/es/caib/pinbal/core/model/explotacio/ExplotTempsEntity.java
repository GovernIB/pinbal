package es.caib.pinbal.core.model.explotacio;

import es.caib.pinbal.core.dto.DiaSetmanaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "pbl_explot_temps")
@Getter @Setter
@Builder @AllArgsConstructor
public class ExplotTempsEntity implements Serializable {

	private static final long serialVersionUID = -2144138256112639860L;

	@Id
	@Column(name = "id")
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data")
	private Date data;
	
	@Column(name = "anualitat")
	private Integer anualitat;
	
	@Column(name = "mes")
	private Integer mes;
	
	@Column(name = "trimestre")
	private Integer trimestre;
	
	@Column(name = "setmana")
	private Integer setmana;
	
	@Column(name = "dia")
	@Enumerated(EnumType.STRING)
	private DiaSetmanaEnum dia;

	public ExplotTempsEntity() {
		super();

		Calendar cal = Calendar.getInstance();
		this.anualitat = cal.get(Calendar.YEAR);
		Integer diaSem = cal.get(Calendar.DAY_OF_WEEK);
		if (diaSem.compareTo(Calendar.MONDAY)==0) { this.dia = DiaSetmanaEnum.LUN; }
		if (diaSem.compareTo(Calendar.TUESDAY)==0) { this.dia = DiaSetmanaEnum.MAR; }
		if (diaSem.compareTo(Calendar.WEDNESDAY)==0) { this.dia = DiaSetmanaEnum.MIE; }
		if (diaSem.compareTo(Calendar.THURSDAY)==0) { this.dia = DiaSetmanaEnum.JUE; }
		if (diaSem.compareTo(Calendar.FRIDAY)==0) { this.dia = DiaSetmanaEnum.VIE; }
		if (diaSem.compareTo(Calendar.SATURDAY)==0) { this.dia = DiaSetmanaEnum.SAB; }
		if (diaSem.compareTo(Calendar.SUNDAY)==0) { this.dia = DiaSetmanaEnum.DOM; }
		this.data = cal.getTime();
		this.setmana = cal.get(Calendar.WEEK_OF_YEAR);
		Integer month = cal.get(Calendar.MONTH);
		if (month.compareTo(Calendar.JANUARY)==0) { this.mes = 1; this.trimestre=1; }
		if (month.compareTo(Calendar.FEBRUARY)==0) { this.mes = 2; this.trimestre=1; }
		if (month.compareTo(Calendar.MARCH)==0) { this.mes = 3; this.trimestre=1; }
		if (month.compareTo(Calendar.APRIL)==0) { this.mes = 4; this.trimestre=2; }
		if (month.compareTo(Calendar.MAY)==0) { this.mes = 5; this.trimestre=2; }
		if (month.compareTo(Calendar.JUNE)==0) { this.mes = 6; this.trimestre=2; }
		if (month.compareTo(Calendar.JULY)==0) { this.mes = 7; this.trimestre=3; }
		if (month.compareTo(Calendar.AUGUST)==0) { this.mes = 8; this.trimestre=3; }
		if (month.compareTo(Calendar.SEPTEMBER)==0) { this.mes = 9; this.trimestre=3; }
		if (month.compareTo(Calendar.OCTOBER)==0) { this.mes = 10; this.trimestre=4; }
		if (month.compareTo(Calendar.NOVEMBER)==0) { this.mes = 11; this.trimestre=4; }
		if (month.compareTo(Calendar.DECEMBER)==0) { this.mes = 12; this.trimestre=4; }
	}
}
