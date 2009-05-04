namespace XmlUnit.Tests.All {
    using System;
    using System.IO;
    using NUnit.Core;

    // This is stolen shamelessly from the source of NUnit.Console
    // Use it if you can't run the entire assembly of tests any other way Jeff

    [Serializable]
    public class AllTests : EventListener {
        public static void Main (string[]args) {
            string assemblyName;
            if (args.Length > 0) {
                assemblyName = args[0];
            } else {
                assemblyName = ".\\xmlunit.tests.dll";
            }
            new AllTests(assemblyName).Run();
        }
        
        private string _assemblyName;
        
        public AllTests(string assemblyName) {
            _assemblyName = assemblyName;
        }
        
        public void Run() {
            TestDomain domain = new TestDomain();
            Test test = domain.LoadAssembly(Path.GetFullPath(_assemblyName), null );
            test.Run(this);
        }
        
        public void TestStarted(TestCase testCase) {}
			
		public void TestFinished(TestCaseResult result) {
		    if (result.IsFailure) {
		        Console.Out.WriteLine("F");
		        Console.Error.WriteLine(result.Message);
		    } else {
		        Console.Out.Write(".");
		    }
		}

		public void SuiteStarted(TestSuite suite) {}

		public void SuiteFinished(TestSuiteResult result) {
		    Console.Out.WriteLine();
		}
        
    }
}
