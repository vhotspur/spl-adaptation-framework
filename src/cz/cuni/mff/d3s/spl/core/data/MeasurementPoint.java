package cz.cuni.mff.d3s.spl.core.data;

public final class MeasurementPoint {
	private int skipFactor;
	private int skipCounter;
	private SampleStorage storage;
	
	public MeasurementPoint(SampleStorage storage, int howManyRunsToSkip) {
		if (storage == null) {
			throw new IllegalArgumentException("Sample storage can not be null.");
		}
		this.storage = storage;
		skipCounter = 0;
		skipFactor = howManyRunsToSkip;
	}
	
	public boolean next() {
		skipCounter++;
		if (skipCounter < skipFactor) {
			return false;
		} else {
			skipCounter = 0;
			return true;
		}
	}
	
	public SampleStorage getStorage() {
		return storage;
	}
}
