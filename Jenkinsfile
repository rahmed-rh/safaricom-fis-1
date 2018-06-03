openshift.withCluster() {
 openshift.withCredentials('cluster-admin-credential-id') {
  def PROJECT_NAME = "poc"
  def cm


  openshift.withProject(PROJECT_NAME) {

   echo "Hello from project ${openshift.project()} in cluster ${openshift.cluster()}"

   // Mark the code checkout 'stage'....
   stage('Creat MySql') {
   def mysqlSvcSelector = openshift.selector("service", "mysql-57-rhel7")
   defm mysqlSvcExists = mysqlSvcSelector.exists()
    if (!mysqlSvcExists) {
	   def app = openshift.newApp('registry.access.redhat.com/rhscl/mysql-57-rhel7','-e MYSQL_USER=user','-e MYSQL_PASSWORD=password','-e MYSQL_DATABASE=sampledb')
    }
	

   }
   node('maven') {
    // Mark the code checkout 'stage'....
    stage('Checkout') {

      // Get some code from a GitHub repository
      git branch: "master", url: cm.data['fis-1-app-git-url']
     }
     	// Mark the code build 'stage'....
    stage('Maven Build') {
	     // Run the maven build
	     sh "mvn clean compile -s settings.xml"
    }
    stage('Deploy to DEV') {
	    //echo "Deleting OLD JDG Env if exist"
	    //openshift.selector( 'all', [ application: cm.data['fis-1-app-name'] ] ).delete()
	    //openshift.selector( 'pvc', [ application: cm.data['fis-1-app-name'] ] ).delete()
     	// Run the fabric8
     	sh "mvn fabric8:deploy -s settings.xml"
    }

   }

  }

 }
}