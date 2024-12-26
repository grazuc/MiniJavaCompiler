///[Error:Z|9]

//test herencia circular en interfaces


interface D extends X {}
interface X extends G{}
interface F{}
interface G extends Z {}
interface Z extends D {}
interface H{}

class C{
    static void main(){}
}


