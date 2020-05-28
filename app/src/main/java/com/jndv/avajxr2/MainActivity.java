package com.jndv.avajxr2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mCreate();
//        mMap();
//        mZip();
//        mConcat();
//        mMerge();
//        mFlatmap();
//        mDistinct();
//        mFilter();
//        mBuffer();
//        mTimer();
//        mInterval();
//        mDoOnnext();
//        mSkip();
//        mTake();
//        mJust();
//        mFromArray();
        mFromIterable();
//        mSingle();
//        mDebounce();
//        mDefer();
//        mLast();
//        mReduce();
//        mScan();
//        mWindow();
    }

    //Create操作符，用于产生一个Observable被观察对象。被观察者Observable称为发射器（上游事件），观察者（Observer）称为接收器（下游事件）
    private void mCreate() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: 发送 1");
                emitter.onNext(1);
                Log.e(TAG, "subscribe: 发送 2");
                emitter.onNext(2);
                //调用此方法，无法接收事件，但是发送事件还是继续的
//                emitter.onComplete();
                Log.e(TAG, "subscribe: 发送 3");
                emitter.onNext(3);
                Log.e(TAG, "subscribe: 发送 4");
                emitter.onNext(4);

            }
        }).subscribe(new Observer<Integer>() {

            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: 接收 " + integer + "  " + mDisposable.isDisposed());
                i++;
                if (i == 2) {
                    //RxJava中，新增的Disposable可以做到切断的操作，让Oberver观察者不再接收上游事件
                    mDisposable.dispose();
                    Log.e(TAG, "onNext: " + mDisposable.isDisposed());
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //总结：
        //1. e.onComplete()方法，使事件无法接收，但发送事件还是继续的
        //2. 2.x中发射事件多了一个throwsException方法，不用再使用try-catch了
        //3. 2.x中多了一个Disposable概念，可以直接调用切断，当isDisposed（）返回false的时候
        //接收器正常接收事件，当为true时，接收器停止接收事件，所以可以通过此参数动态控制接收事件

    }

    //map是RxJava中一个最简单的操作符了，对发射事件发送的每一个事件应用一个函数，使每一个事件都按照指定的函数去变化
    private void mMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "accept: " + s);
            }
        });

        //总结：map的基本作用就是将一个Observable通过某种函数关系，转换成另一种Observable，上面的例子就是把
        //Integer数据变成了String类型
    }

    //zip专用于合并事件，该合并不是连接，而是两两配对，这就意味着，最终配对出的Observable发射事件数目只和少的那个相同
    private void mZip() {
        Observable.zip(getStringObservable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }

        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "accept: " + s);
            }
        });

        //总结
        //1.zip组合事件的过程就是分别从发射器A和发射器B各取出一个事件来组合，并且一个事件只能被使用一次
        //组合的顺序使严格按照事件的发送顺序来进行的。
        //2.最终接收器收到的事件数量是和发送器发送事件最少的那个发送器事件数目相同
    }

    private Observable getStringObservable() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    Log.e(TAG, "subscribe: A");
                    emitter.onNext("A");
                    Log.e(TAG, "subscribe: B");
                    emitter.onNext("B");
                    Log.e(TAG, "subscribe: C");
                    emitter.onNext("C");
                }
            }
        });
    }

    private Observable getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    Log.e(TAG, "subscribe: 1");
                    emitter.onNext(1);
                    Log.e(TAG, "subscribe: 2");
                    emitter.onNext(2);
                    Log.e(TAG, "subscribe: 3");
                    emitter.onNext(3);
                    Log.e(TAG, "subscribe: 4");
                    emitter.onNext(4);
                    Log.e(TAG, "subscribe: 5");
                    emitter.onNext(5);
                }
            }
        });
    }

    //Concat方法，单一的把两个发射器连接成一个发射器
    private void mConcat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
        //总结：发射器B把自己的三个事件发送给了发射器A，让他们组合成了一个新的发射器
    }

    //merge:作用是把多个Observable结合起来，接受可变参数，也支持迭代器集合，
    // 它和concat的区别在于，不用等到发射器A发送完所有的事件再进行发射器B的发送
    private void mMerge() {
        Observable.merge(Observable.just(1, 2), Observable.just(6, 7, 8, 9, 60))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //FlatMap方法，很有趣，实际开发中经常用到，可以把一个发射器Observable通过某种方法，转换为多个Observables，然后再把这些
    //分散的Observables装进一个单一的发射器Observable，但有个需要注意的是，flatmap并不能保证事件的顺序，如果需要保证，就要用到ConcatMap
    private void mFlatmap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {//可直接替换成concatMap
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value" + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MICROSECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });

        //总结：使用FlatMap通过查看log日志验证它是无序的，替换成concatMap发现，是有序的

    }

    //distinct 去重
    private void mDistinct() {
        Observable.just(1, 1, 2, 3, 3, 4, 5, 6, 6, 6)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }


    //Filter:过滤器，接受一个参数，过滤掉不符合我们条件的值
    private void mFilter() {
        Observable.just(1, 20, -3, -5, 10, 38)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer >= 10;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: " + integer);
            }
        });
    }

    //buffer:接受两个参数buffer(count,skip)，作用是将Observable中的数据按skip（步长）分成最大不超过count的buffer，
    //然后生成一个Observable。
    private void mBuffer() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e(TAG, "accept: buffer size : " + integers.size());
                        for (int i = 0; i < integers.size(); i++) {
                            Log.e(TAG, "accept: " + integers.get(i));
                        }
                    }
                });

        //总结：我们把 1, 2, 3, 4, 5 依次发射出来，经过 buffer 操作符，其中参数 skip 为 2， count 为 3，而我们的输出 依次是 123，345，5。显而易见，我们 buffer 的第一个参数是 count，代表最大取值，在事件足够的时候，一般都是取 count 个值，然后每次跳过 skip 个事件
    }

    //timer:定时任务，在1.x中它还可以执行间隔逻辑，但是在2.x中此功能被交给了interval
    //timer和interval默认在新线程
    private void mTimer() {
        Log.e(TAG, "accept: start " + " at " + System.currentTimeMillis() / 1000);
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//timer默认在新线程，所以要切换回主线程
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: " + aLong + " at " + System.currentTimeMillis() / 1000);
                    }
                });
        //总结：通过日志发现，事件接收被延迟了2秒
    }

    //interval:用于间隔时间执行某个操作（定时任务），接收三个参数，分别是，第一次发送延迟，间隔时间，时间单位
    private Disposable mDisosable;

    private void mInterval() {
        mDisosable = Observable.interval(3, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//由于interva默认在新线程，所以我们应该切回主线程
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "accept: " + aLong + " at " + System.currentTimeMillis() / 1000);
                    }
                });

        //总结：通过观察日志发现第一次延迟了 3 秒后接收到，后面每次间隔了 2 秒。
        //但是，由于我们这个是间隔执行，所以当我们的Activity都销毁的时候，实际上这个操作还依然在进行
        //所以，我们要在不需要的时候，干掉它。查看源码发现，我们subscribe(Cousumer<? super T> onNext)返回的是Disposable，我们可以在这上面做文章(详见onDestroy())。
    }

    //doOnNext不算一个操作符，但是考虑到其常用性，可以视为操作符
    //它的作用是让订阅者在接收到数据之前干点有意思的事情，比如，我们在获取到数据之前想先保存一下它
    private void mDoOnnext() {
        Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: 保存 " + integer + " 成功");
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: 收到 " + integer);
            }
        });
    }

    //skip:接收一个long型参数count，代表跳过count个数目开始接收
    private void mSkip() {
        Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //take:接收一个long型参数count，代表最多接收count个数据(这里用到了一个Flowable)
    private void mTake() {
        Flowable.fromArray(1, 2, 3, 4, 5)
                .take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //just:就不用多说了，就是一个简单的发射器，依次调用onNext（），参数为1到10个
    private void mJust() {


        Observable
                .just(1, 2, 3, 4, 5, 6, 7)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "this is " + integer;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        Log.e(TAG, "accept: " + string);
                    }
                });

        int[] arrays1 = {1, 2, 3};
        int[] arrays2 = {4, 5, 6};
        Observable.just(arrays1, arrays2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<int[]>() {
                    @Override
                    public void accept(int[] ints) throws Exception {
                        for (Integer integer : ints)
                            Log.e(TAG, "accept: " + integer);
                    }
                });
    }


    //fromArray:接收一个数组，从数组中一个一个取出来发射
    private void mFromArray() {
//        上游数组，下游也数组
//        int i[] = {1, 2, 3, 4, 5, 6, 7, 23, 5, 56, 43};
//        Observable.fromArray(i)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<int[]>() {
//                    @Override
//                    public void accept(int[] ints) throws Exception {
//                        for (Integer integer : ints)
//                            Log.e(TAG, "accept: "+integer);
//                    }
//                });

//        上游数组，下游直接遍历
        Integer[] integers = {1, 2, 3, 4, 5, 6, 7, 23, 5, 56, 43};
        Observable.fromArray(integers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //fromIterable:接收一个list，然后逐个发射
    private void mFromIterable() {
        int i[] = {1, 2, 3, 4, 5, 6, 7, 23, 5, 56, 43};
        Observable.fromIterable(Arrays.asList(i))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<int[]>() {
                    @Override
                    public void accept(int[] ints) throws Exception {
                        for (Integer integer : ints)
                            Log.e(TAG, "accept: " + integer);
                    }
                });

        Integer integers[] = {1, 2, 3, 4, 5, 6, 7, 23, 5, 56, 43};
        Observable.fromIterable(Arrays.asList(integers))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }


    //single:single只会接收一个参数，SingleObserver只会调用onError或者onSucess
    private void mSingle() {
        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e(TAG, "onSuccess: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.toString());

                    }
                });

    }

    //debounce:去除发送频率过快的项，看起来好像没啥用，后面用处很大
    private void mDebounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(400);
                emitter.onNext(2);
                Thread.sleep(505);
                emitter.onNext(3);
                Thread.sleep(100);
                emitter.onNext(4);
                Thread.sleep(605);
                emitter.onNext(5);
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
        //总结：去除发送间隔小于500毫秒的发射事件，所以1和3去掉了
    }


    //defer：每次订阅都会创建一个新的Observable，并且如果没有被订阅，就不会产生新的Observable
    private void mDefer() {
        Observable observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1, 2, 3);
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: " + integer);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //last:仅取出可观察到的最后一个值，或者满足某些条件的最后一个项
    private void mLast() {
        Observable.just(1, 2, 3)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //reduce:每次用一个方法处理一个值，可以有一个seed作为初始值
    private void mReduce() {
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {

                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });

        //总结：reduce，支持一个function为两数值相加，所以应该最后的值是：1+2=3+3=6
    }

    //scan：和上面的reduce一致，区别是，reduce只追求结果，而scan会始终如一的把每一个步骤都输出
    private void mScan() {
        Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {

                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });
    }

    //window:按照实际划分窗口，将数据发送给不同的Observable
    private void mWindow() {
        Observable.interval(1, TimeUnit.SECONDS)//间隔1秒发一次
                .take(15)//最多接收15个
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        Log.e(TAG, "accept: Sub Divide begin");
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        Log.e(TAG, "accept: " + aLong);
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisosable != null && !mDisosable.isDisposed()) {
            mDisosable.dispose();
        }
    }
}
