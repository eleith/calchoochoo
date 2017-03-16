package com.eleith.calchoochoo.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

//rx observer as an event bus: http://blog.kaush.co/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
public class RxBus {

  private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

  public void send(Object o) {
    bus.onNext(o);
  }

  public Observable<Object> toObservable() {
    return bus;
  }

  public <Event extends Object> Observable<Event> observeEvents(Class<Event> eventClass) {
    return bus.ofType(eventClass);
  }

  public boolean hasObservers() {
    return bus.hasObservers();
  }
}
