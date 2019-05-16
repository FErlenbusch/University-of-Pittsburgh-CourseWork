package myauction.data_models;

import java.sql.Date;

public class Product {
	private int auctionId;
    private String name;
    private String description;
    private String seller;
    private int minPrice;
    private int numberOfDays;
    private String status;
    private String buyer;
    private Date sellDate;
    private int amount;

	public Product(int auctionId, String name, String description, String seller, int minPrice, int numberOfDays,
			String status, String buyer, Date sellDate, int amount) {
		super();
		this.auctionId = auctionId;
		this.name = name;
		this.description = description;
		this.seller = seller;
		this.minPrice = minPrice;
		this.numberOfDays = numberOfDays;
		this.status = status;
		this.buyer = buyer;
		this.sellDate = sellDate;
		this.amount = amount;
	}

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public Date getSellDate() {
		return sellDate;
	}

	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String toString() {
		String prodString = "\n";
		prodString = prodString + name;
		prodString = prodString + "\t" + description;
		prodString = prodString + "\t" + seller;
		prodString = prodString + "\t" + minPrice;
		prodString = prodString + "\t" + numberOfDays;
		prodString = prodString + "\t" + status;
		prodString = prodString + "\t" + buyer;
		prodString = prodString + "\t" + sellDate;
		prodString = prodString + "\t" + amount;
		return prodString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + auctionId;
		result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + minPrice;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + numberOfDays;
		result = prime * result + ((sellDate == null) ? 0 : sellDate.hashCode());
		result = prime * result + ((seller == null) ? 0 : seller.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (amount != other.amount)
			return false;
		if (auctionId != other.auctionId)
			return false;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (minPrice != other.minPrice)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numberOfDays != other.numberOfDays)
			return false;
		if (sellDate == null) {
			if (other.sellDate != null)
				return false;
		} else if (!sellDate.equals(other.sellDate))
			return false;
		if (seller == null) {
			if (other.seller != null)
				return false;
		} else if (!seller.equals(other.seller))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
}
