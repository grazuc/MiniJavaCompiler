///[Error:C|16]
///La clase D tiene un constructor definido con otro nombre

class B {
    public B(){
    }
}

class C extends B{
    public C(){

    }
}

class D {
    public C(){}
}

class Init{
    static void main()
    { }
}