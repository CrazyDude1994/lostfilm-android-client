package com.crazydude.interactors;

import io.reactivex.Observable;

/**
 * Interactor interface is a application domain logic
 *
 * @param <O> output data type. Will be returned in result callback
 */
public interface Interactor<O> {

    Observable<O> getObservable();
}
