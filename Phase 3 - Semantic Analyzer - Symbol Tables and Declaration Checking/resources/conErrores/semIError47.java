///[Error:met|20]

class A {

    B x;
    static void main(){}

    B met(B x){}

}

interface B {

    C met(C x);

}

interface C extends B {

    B met(B x);
}