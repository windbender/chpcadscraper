package com.github.windbender.chpcadscraper;

import org.joda.time.DateMidnight;

public class EventKey implements Comparable<EventKey> {
	Integer id;
	DateMidnight day;
	public EventKey(String chipidStr, DateMidnight dateMidnight) {
		this.id = Integer.parseInt(chipidStr);
		this.day = dateMidnight;
	}
	@Override
	public String toString() {
		return "EventKey [id=" + id + ", day=" + day + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EventKey other = (EventKey) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public int compareTo(EventKey o) {
		int x = o.day.getDayOfYear() - this.day.getDayOfYear();
		if(x == 0) {
			x = o.id.intValue() - this.id.intValue();
		}
		
		return x;
	}
	

}
