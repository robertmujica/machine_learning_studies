package Predictor;
import java.util.Date;


public class StockData {
	private int id;
	private Date dateIndex;
	private double ActualClose;
	private double normalizedActual;
	
	public Date getDateIndex() {
		return dateIndex;
	}
	public void setDateIndex(Date dateIndex) {
		this.dateIndex = dateIndex;
	}
	public double getAdjClose() {
		return ActualClose;
	}
	public void setAdjClose(double adjClose) {
		this.ActualClose = adjClose;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getNormalizedActual() {
		return normalizedActual;
	}
	public void setNormalizedActual(double normalizedActual) {
		this.normalizedActual = normalizedActual;
	}
}
