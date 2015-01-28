package builder.data;

import models.sra.submit.common.instance.Sample;


public class SampleBuilder {

	Sample sample = new Sample();
	
	public SampleBuilder withCode(String code)
	{
		sample.code=code;
		return this;
	}
	
	public Sample build()
	{
		return sample;
	}
}