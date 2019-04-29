package com.ajeyone.rxjavastudy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

public class RxJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_takeWhile)
    void onDemoTakeWhile() {
        Observable.range(0, 10)
                .takeWhile(value -> value % 2 == 0)
                .subscribe(new BasicObserver<>());
    }


    @SuppressLint("CheckResult")
    @OnClick(R.id.button_all)
    void onDemoAll() {
        Observable.range(1, 10)
                .all(aLong -> aLong < 10)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        logthread("all result: " + aBoolean);
                    }
                });
    }


    @OnClick(R.id.button_repeat)
    void onDemoRepeat() {
        Observable.intervalRange(0, 10, 0, 300, TimeUnit.MILLISECONDS)
                .repeat(2)
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button_amb)
    void onDemoAmb() {
        Observable.ambArray(
                Observable.rangeLong(1, 5).delay(500, TimeUnit.MILLISECONDS),
                Observable.intervalRange(100, 8, 460, 200, TimeUnit.MILLISECONDS)
        ).subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button_debounce)
    void onDemoDebounce() {
        Observable.intervalRange(0, 10, 0, 300, TimeUnit.MILLISECONDS)
                .debounce(new Function<Long, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Long aLong) throws Exception {
//                        logthread("In the selector: " + aLong);
                        if (aLong % 2 == 0) {
                            return Observable.empty();
                        } else {
                            return Observable.never();
                        }
                    }
                })
//                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button_filter)
    void onDemoFilter() {
        Observable.range(0, 100)
                .filter(integer -> integer % 7 == 0)
                .filter(integer -> integer % 5 == 0)
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button_observeOn)
    void onDemoObserveOn() {
        Observable.range(0, 10)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .map(integer -> {
                    logthread("map 1: " + integer + " to String");
                    return "A" + integer.toString();
                })
                .observeOn(Schedulers.newThread())
                .map(string -> {
                    logthread("map 2: " + string + " to String");
                    return "[" + string + "]";
                })
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button17)
    void onDemoCombineLatest() {
        Observable.combineLatest(
                Observable.intervalRange(1, 10, 0, 600, TimeUnit.MILLISECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String r = "A" + String.valueOf(aLong);
                                logthread(r);
                                return r;
                            }
                        }),
                Observable.intervalRange(1, 6, 0, 1000, TimeUnit.MILLISECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String r = "B" + String.valueOf(aLong);
                                logthread(r);
                                return r;
                            }
                        }),
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String a, String b) throws Exception {
                        return a + " | " + b;
                    }
                }
        ).subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button16)
    void onDemoZip() {
        Observable.zip(
                Observable.intervalRange(0, 20, 0, 200, TimeUnit.MILLISECONDS),
                Observable.intervalRange(0, 10, 0, 3000, TimeUnit.MILLISECONDS),
                new BiFunction<Long, Long, String>() {
                    @Override
                    public String apply(Long integer, Long integer2) throws Exception {
                        integer2 = 10 - integer2;
                        return integer + "x" + integer2 + "=" + (integer * integer2);
                    }
                })
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button1)
    void onDemoCreate() {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            emitter.onNext("ancdefg");
            emitter.onNext("123456");
            emitter.onNext("android");
            emitter.onNext("ios");
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button2)
    void onDemoJust() {
        Observable.just("1", "2", "3", "4.1", "4.2").subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button3)
    void onDemoFromArray() {
        Observable.fromArray(new Integer[]{1, 2, 3, 4, 5, 6}).subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button4)
    void onDemoFromCallable() {
        new Thread() {
            @Override
            public void run() {
                Observable.fromCallable(() -> {
                    long t = new Random().nextInt(3000) + 2000;
                    try {
                        logthread("callable");
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "X-Y-Z:" + t;
                })
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(d -> {
                            logthread("doOnSubscribe");
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            logthread("doOnComplete");
                        })
                        .subscribe(new BasicObserver<>());
            }
        }.start();
    }

    @OnClick(R.id.button5)
    void onDemoFuture() {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            logthread("in future callable");
            return "Hehe...";
        });
        Observable.fromFuture(futureTask)
                .doOnSubscribe(d -> {
                    logthread("doOnSubscribe");
                    futureTask.run();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button6)
    void onDemoFromInterable() {
        List<String> list = Arrays.asList("1.1", "1.2", "1.3");
        Observable.fromIterable(list).subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button7)
    void onDemoDefer() {
        Integer[] numbers = new Integer[]{123};
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                int n = numbers[0];
                return Observable.just(n, n + 1, n + 2);
            }
        });
        numbers[0] += 9000;
        observable.subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button8)
    void onDemoTimer() {
        Observable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button9)
    void onDemoMap() {
        Observable.just(1, 2, 4, 5, 6)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "[" + integer + "]";
                    }
                })
                .buffer(9)
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button10)
    void onDemoConcatMap() {
        Observable.range(1, 9)
                .concatMap(new Function<Integer, ObservableSource<Integer[]>>() {
                    @Override
                    public ObservableSource<Integer[]> apply(Integer integer) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Integer[]>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer[]> emitter) throws Exception {
                                for (int i = 1; i <= 9; i++) {
                                    emitter.onNext(new Integer[]{integer, i});
                                }
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .map(new Function<Integer[], String>() {
                    @Override
                    public String apply(Integer[] integers) throws Exception {
                        return integers[0] + "x" + integers[1] + "=" + (integers[0] * integers[1]);
                    }
                })
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button11)
    void onDemoBuffer() {
        Observable.range(0, 9)
                .flatMap((Function<Integer, ObservableSource<Integer>>) i -> {
                    return Observable.range(i * 9, 9);
                })
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer + 1;
                    }
                })
                .buffer(9)
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button12)
    void onDemoGroupBy() {
        Observable.range(1, 27)
                .groupBy(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer % 3;
                    }
                })
                .subscribe(new Observer<GroupedObservable<Integer, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) {
                        integerIntegerGroupedObservable.subscribe(new KeyedObserver<>(integerIntegerGroupedObservable.getKey()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.button13)
    void onDemoScan(Button button) {
        if ("reduce() ...".equals(button.getText().toString())) {
            Disposable d = Observable.intervalRange(1, 9, 0, 500, TimeUnit.MILLISECONDS)
                    .reduce(new BiFunction<Long, Long, Long>() {
                        @Override
                        public Long apply(Long integer, Long integer2) throws Exception {
                            logthread("applying: " + integer + ", " + integer2);
                            return integer + integer2;
                        }
                    })
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            logthread("accept: " + aLong);
                        }
                    });
            button.setText("scan() ...");
        } else {
            Observable.intervalRange(1, 9, 0, 500, TimeUnit.MILLISECONDS)
                    .scan(new BiFunction<Long, Long, Long>() {
                        @Override
                        public Long apply(Long integer, Long integer2) throws Exception {
                            logthread("applying: " + integer + ", " + integer2);
                            return integer + integer2;
                        }
                    })
                    .subscribe(new BasicObserver<>());
            button.setText("reduce() ...");
        }
    }

    @OnClick(R.id.button14)
    void onDemoConcat() {
        Observable.concat(
                Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS),
                Observable.intervalRange(100, 10, 0, 1, TimeUnit.SECONDS))
                .subscribe(new BasicObserver<>());
    }

    @OnClick(R.id.button15)
    void onDemoMerge() {
        Observable.merge(
                Observable.intervalRange(0, 10, 0, 300, TimeUnit.MILLISECONDS),
                Observable.intervalRange(100, 10, 0, 500, TimeUnit.MILLISECONDS))
                .subscribe(new BasicObserver<>());
    }

    private static void logthread(String message) {
        Log.d("ajeyoneRxJava", message + " | " + Thread.currentThread().getName());
    }
}
