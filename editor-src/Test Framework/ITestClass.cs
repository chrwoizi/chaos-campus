using System;
using System.Collections.Generic;
using System.Text;

namespace Test_Framework
{
    interface ITestClass
    {
        bool ExecuteTest();

        string InfoMessage
        { 
            get;
        }
        string TestName
        {
            get;
        }
    }
}
