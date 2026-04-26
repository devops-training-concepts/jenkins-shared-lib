def call(String imageName) {

    def tag = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

    sh """
        docker build -t ${imageName}:${tag} .
        docker tag ${imageName}:${tag} ${imageName}:latest
    """

    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
        usernameVariable: 'USER',
        passwordVariable: 'PASS')]) {

        sh """
            echo $PASS | docker login -u $USER --password-stdin
            docker push ${imageName}:${tag}
            docker push ${imageName}:latest
        """
    }

    return tag
}