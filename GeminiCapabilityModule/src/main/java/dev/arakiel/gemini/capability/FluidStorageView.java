package dev.arakiel.gemini.capability;

public interface FluidStorageView {
    int tanks();

    GeminiFluidStack getTank(int tank);

    long capacity(int tank);

    long fill(int tank, GeminiFluidStack resource, long amount, boolean simulate);

    GeminiFluidStack drain(int tank, long amount, boolean simulate);
}
