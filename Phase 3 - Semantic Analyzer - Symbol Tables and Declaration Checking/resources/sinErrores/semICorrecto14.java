

interface I {
    void met1();
    String met2();
    A met3();
}

class A {
    Object atrObject;
    String w;

    static void main(){}
    B metodoA(B x, B y, B z){}
}

class B extends A {

    Object x;
    String atrString;

    void met1(){}
    String met2(){}
    A met3(){}
    char metj(int x){}
    void metW(String x, int y){}
}

interface J extends W{
    char metj(int x);
}

interface W {
    void metW(String x, int y);
}