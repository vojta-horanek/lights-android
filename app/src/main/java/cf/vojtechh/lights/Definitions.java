package cf.vojtechh.lights;

abstract class State {
    static final int Off = 0;
    static final int On = 1;
    static final int Err = 2;
}

abstract class Code {
    static final int Off = 0;
    static final int On = 1;
    static final int Switch = 2;
    static final int State = 3;
    static final int Quit = 4;
}
