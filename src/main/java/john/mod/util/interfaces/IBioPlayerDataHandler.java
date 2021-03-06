package john.mod.util.interfaces;

import john.mod.BioElements;

public interface IBioPlayerDataHandler
{
    BioElements getElement();
    void setElement(BioElements element);
    void removeElement();

    Boolean getMaskActive();
    void setMaskActive(Boolean status);

	int getMaskCharge();
	void setMaskCharge(int charge);
    boolean modifyMaskCharge(int modifier);
}
