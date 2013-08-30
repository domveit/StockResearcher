package org.djv.stockresearcher.model;

public class Industry {
	
	private Long id;
	private Long sectorId;
	private String desc;
	
	public Industry(Long id, Long sectorId, String desc) {
		super();
		this.id = id;
		this.sectorId = sectorId;
		this.desc = desc;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Long getSectorId() {
		return sectorId;
	}
	public void setSectorId(Long sectorId) {
		this.sectorId = sectorId;
	}
	
}
