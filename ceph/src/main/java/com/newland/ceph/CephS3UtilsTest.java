package com.newland.ceph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.util.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class CephS3UtilsTest {
	
	//服务器
//	public static String accessKey = "P6V7K1979A44B4X8QSJD";
//	public static String secretKey = "QBMAkpg0FYMdXRahoU5OQkiUNDT2GzymJ2Rem1tz";
//	public static String endPoint = "http://10.1.12.173:7480";
//	public static String accessKey = "45RQJZZ98U3JITE2MRIQ";
	public static String accessKey = "45RQJZZ98U3JITE2MRIQ";
//	public static String secretKey = "mNE4fLYs2i2a7uFcLXRPD53ziNC2xvZByOxCaYNc";
	public static String secretKey = "PNg5igt3ria44KpEIDcVYWeNQv1L7aIPJDRbSxu7";
	public static String endPoint = "http://10.1.12.173:7480";
	//私有
//	public static String accessKey = "9WD7TXROJ9A4ETGZ6RON";
//	public static String secretKey = "vQOvUXtXy34OCeNrDgpPFJ5Xs1L0ibaeko0QaMCs";
//	public static String endPoint = "http://10.1.12.146:7480";

	public static String bucketName = "ptam.test";// + UUID.randomUUID();
	public static String key = "MyObjectKey" + UUID.randomUUID();
//	public static String key = "MyObjectKey7a4f0ba7-8b07-4b7c-a2bd-f651ad4dd52f";
//	public static String key = "a/b/c";
	public static AmazonS3 s3Conn = null;
	
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");

        try {
        	ConnConfig cfg = new ConnConfig();
        	cfg.setAccessKey(accessKey);
        	cfg.setSecretKey(secretKey);
        	cfg.setEndPoint(endPoint);
        	cfg.setBucketName(bucketName);
        	
        	createBucket(cfg);

        	while (true){
                key = "MyObjectKey" + UUID.randomUUID();
                uploadObject(cfg, key, createSampleFile());
//            downloadObject2(cfg, key);
                displayTextInputStream(downloadObject(cfg, key));
                Thread.sleep(1000);
            }

//        	deleteObject(cfg, key);
//
//        	deleteBucket(cfg);
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    /**
     * Creates a temporary file with text data to demonstrate uploading a file
     * to Amazon S3
     *
     * @return A newly created temporary file with text data.
     *
     * @throws IOException
     */
    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901235\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        return file;
    }

    /**
     * Displays the contents of the specified input stream as text.
     *
     * @param input
     *            The input stream to display as text.
     *
     * @throws IOException
     */
    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }
    
    private static AmazonS3 getAmazonS3() {
        if (s3Conn == null) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            s3Conn = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, "")).build();
            System.out.println(s3Conn);
        }
        return s3Conn;
    }
    
    private static void init(ConnConfig cfg) {
    	accessKey = cfg.getAccessKey();
    	secretKey = cfg.getSecretKey();
    	endPoint = cfg.getEndPoint();
    	bucketName = cfg.getBucketName();
    }
    static class ConnConfig{
        private String accessKey;
        private String secretKey;
        private String endPoint;
        private String bucketName;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
    
    private static AmazonS3 getAmazonS32() {
    	AWSCredentials credentials = null; 

        try {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

    	ClientConfiguration clientConfig = new ClientConfiguration();
    	clientConfig.setProtocol(Protocol.HTTP);
    	clientConfig.setSignerOverride("S3SignerType");

    	AmazonS3 s3 = new AmazonS3Client(credentials, clientConfig);
        s3.setEndpoint(endPoint);
        
        return s3;
    }
    
    public static void createBucket(ConnConfig cfg) {
    	try {
    		init(cfg);
    		System.out.println("Listing buckets");
            for (Bucket bucket : getAmazonS3().listBuckets()) {
                System.out.println(" - " + bucket.getName());
                if(bucketName.equals(bucket.getName()))
                	return;
            }
    		System.out.println("Creating bucket " + bucketName + "\n");
    		Bucket bucket = getAmazonS3().createBucket(bucketName);
    	}  catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    
    public static void deleteBucket(ConnConfig cfg) {

    	try {
    		init(cfg);
    	      System.out.println("Deleting bucket " + bucketName + "\n");
    	      getAmazonS3().deleteBucket(bucketName);
    	}  catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    
    public static Boolean uploadObject(ConnConfig cfg, String key, File file) {
    	System.out.println("Uploading a new object to S3 from a file, key=" + key);
    	init(cfg);
    	getAmazonS3().putObject(new PutObjectRequest(bucketName, key, file));
    	
    	return true;
    }
    
    public static InputStream downloadObject(ConnConfig cfg, String key) {
    	System.out.println("Downloading an object");
    	init(cfg);
        S3Object object = getAmazonS3().getObject(new GetObjectRequest(bucketName, key));
        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
        
        return object.getObjectContent();
    }


    public static void downloadObject2(ConnConfig cfg, String key) {
        System.out.println("Downloading an object");
        init(cfg);
        getAmazonS3().getObject(new GetObjectRequest(bucketName, key),new File("bb.eep"));
    }
    
    public static Boolean deleteObject(ConnConfig cfg, String key) {
    	try {
    		System.out.println("Deleting an object\n");
    		init(cfg);
        	getAmazonS3().deleteObject(bucketName, key);
    	}  catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    	return true;
    }

}