///101&11&33&22&1&exitosamente

class MainModule{

    int mainInt;

    static void main(){
        var a = new A();
        a.init();
        System.printIln(a.enteroX());
        System.printIln(a.publicA1);
        System.printIln(a.publicX1);
        System.printIln(a.getPrivateA1());
        a.setPrivateX1(1);
        System.printIln(a.getPrivateX1());
    }
}

class A extends X{

    int publicA1;
    int privateA1;

    void init(){
        publicA1 = 11;
        privateA1 = 22;
        publicX1 = 33;
    }

    int enteroA(){
        return 99;
    }

    int getPrivateA1(){ return privateA1; }
}

class X{

    int publicX1;
    int privateX1;

    int enteroX(){
        return 101;
    }

    void setPrivateX1(int privateX1){ this.privateX1 = privateX1; }

    int getPrivateX1(){ return privateX1; }

}