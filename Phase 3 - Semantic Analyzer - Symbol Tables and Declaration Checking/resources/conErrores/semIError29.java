///[Error:metodox|18]
//metodo mal redefinido
interface X {
    int metodox();
}
interface Y{
    int metodoy();
}
interface Z{
    int metodoz();
}
class A {
    int metodox(){}
    int metodoy(){}
}

class B extends A {
    char metodox(){}
    int metodoz(){}
    static void main(){}
}