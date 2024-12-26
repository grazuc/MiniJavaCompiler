interface X {
    int metodoz();
}
interface Y{
    int metodoy();
}
interface Z extends X {
    int metodoz();
}

class A {
    int metodox(){}
    int metodoy(){}
}

class B implements Z {

    int metodoz(){}
    static void main(){}
}