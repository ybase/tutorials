package com.baeldung.rxjava;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;
import rx.Observable.Operator;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.functions.Func1;

import com.baelding.rxjava.operator.cleanString;
import com.baelding.rxjava.operator.toLength;

public class RxJavaCustomOperatorUnitTest {

    @Test
    public void whenUseCleanStringOperator_thenSuccess() {
        final List<String> list = Arrays.asList("john_1", "tom-3");
        final List<String> results = new ArrayList<String>();

        final Observable<String> observable = Observable.from(list)
            .lift(new cleanString());

        // when
        observable.subscribe(results::add);

        // then
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        assertThat(results, hasItems("john1", "tom3"));
    }

    @Test
    public void whenUseToLengthOperator_thenSuccess() {
        final List<String> list = Arrays.asList("john", "tom");
        final List<Integer> results = new ArrayList<Integer>();

        final Observable<Integer> observable = Observable.from(list)
            .compose(new toLength());

        // when
        observable.subscribe(results::add);

        // then
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        assertThat(results, hasItems(4, 3));
    }

    @Test
    public void whenUseFunctionOperator_thenSuccess() {
        final Operator<String, String> cleanStringFn = subscriber -> {
            return new Subscriber<String>(subscriber) {
                @Override
                public void onCompleted() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(t);
                    }
                }

                @Override
                public void onNext(String str) {
                    if (!subscriber.isUnsubscribed()) {
                        final String result = str.replaceAll("[^A-Za-z0-9]", "");
                        subscriber.onNext(result);
                    }
                }
            };
        };

        final List<String> results = new ArrayList<String>();
        Observable.from(Arrays.asList("ap_p-l@e", "or-an?ge"))
            .lift(cleanStringFn)
            .subscribe(results::add);

        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        assertThat(results, hasItems("apple", "orange"));
    }

    @Test
    public void whenUseFunctionTransformer_thenSuccess() {
        final Transformer<String, Integer> toLengthFn = source -> {
            return source.map(new Func1<String, Integer>() {
                @Override
                public Integer call(String str) {
                    return str.length();
                }
            });
        };

        final List<Integer> results = new ArrayList<Integer>();
        Observable.from(Arrays.asList("apple", "orange"))
            .compose(toLengthFn)
            .subscribe(results::add);

        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        assertThat(results, hasItems(5, 6));
    }
}
