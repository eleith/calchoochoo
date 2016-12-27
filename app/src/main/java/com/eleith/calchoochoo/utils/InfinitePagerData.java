package com.eleith.calchoochoo.utils;

public class InfinitePagerData<T> {
  private T[] dataArray;
  private T temporarySavedData;

  public InfinitePagerData(T[] dataArray) {
    setDataArray(dataArray);
  }

  public void setDataArray(T[] dataArray) {
    this.dataArray = dataArray;
  }

  public void shiftRight() {
    int size = dataArray.length;
    temporarySavedData = dataArray[0];

    for (int i = 0; i < size - 1; i++) {
      dataArray[i] = dataArray[i + 1];
    }

    dataArray[size - 1] = getNextData();
  }

  public T getNextData() {
    return temporarySavedData;
  }

  public void shiftLeft() {
    int size = dataArray.length;
    temporarySavedData = dataArray[size - 1];

    for (int i = dataArray.length - 1; i > 0; i--) {
      dataArray[i] = dataArray[i - 1];
    }

    dataArray[0] = getPreviousData();
  }

  public T getPreviousData() {
    return temporarySavedData;
  }

  public T getData(int position) {
    return dataArray[position];
  }

  public int getDataSize() {
    return dataArray.length;
  }

  public String getTextFor(int position) {
    return dataArray[position].toString();
  }
}
