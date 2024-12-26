

class A implements D {
    B x;
    C y;
    D metodo(){}
    A metodoD(){}
    D metodoC(C x, D y){}
}

class B extends Object {
    static void main(){}
    C metodoB(){}
}

interface C extends D{
    D metodoC(C x, D y);
}

interface D {
    A metodoD();
}