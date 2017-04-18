package com.crazydude.interactors;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CrazyDude on 4/18/17.
 */

public abstract class ObservableInteractor<I, O> implements Interactor<O> {

    private I mData;
    private Scheduler mScheduler;

    public ObservableInteractor(I data, Scheduler observeScheduler) {
        mData = data;
        mScheduler = observeScheduler;
    }

    @Override
    public Observable<O> getObservable() {
        return buildObservable(mData)
                .subscribeOn(Schedulers.io())
                .observeOn(mScheduler);
    }

    protected abstract Observable<O> buildObservable(I data);
}
