///[Error:Z|17]
//la clase B no implementa todos los metodos de Z (falta implementar metodoz)
interface X {
    int metodox();
}
interface Y{
    int metodoy();
}
interface Z extends Y {
    int metodoz();
}
class A {
    int metodox(){}
    int metodoy(){}
}

class B implements Z {

    static void main(){}
}