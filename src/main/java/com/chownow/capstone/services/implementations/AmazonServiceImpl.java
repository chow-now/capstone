package com.chownow.capstone.services.implementations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.chownow.capstone.models.Image;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.ImageRepository;
import com.chownow.capstone.repos.UserRepository;
import com.chownow.capstone.services.AmazonService;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AmazonServiceImpl implements AmazonService {

    private AmazonS3 s3Client;
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonServiceImpl.class);

    @Value("${aws.s3.endpoint.url}")
    private String s3Endpoint;

    @Value("${aws.s3.access.key}")
    private String s3Access;

    @Value("${aws.s3.secret.key}")
    private String s3Secret;

    @Value("${aws.s3.bucket.name}")
    private String s3Bucket;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private ImageRepository imageDao;

    @PostConstruct
    private void initializeAmazon() {
        this.s3Client = new AmazonS3Client(new BasicAWSCredentials(s3Access, s3Secret));
    }

    @Override
    @Async
    public String uploadAvatar(MultipartFile multipartFile, User user) {
        String fileUrl = "";
        JSONObject jsonObject = new JSONObject();
        try {
            /* get converted file to upload */
            File file = convertMultiPartToFile(multipartFile);
            /* set timestamp for unique filename */
            long timeStamp = new Date().getTime();
            /* set the filepath to save file */
            String filepath = "users/"+user.getId()+"/avatar/";
            /* add filepath to filename to map directory in s3 */
            String fileName =
                    filepath +
                    timeStamp + "-" +
                    multipartFile.getOriginalFilename().replace(" ", "_");
            /* create user avatar url for db */
            fileUrl = s3Endpoint+"/"+s3Bucket+"/"+fileName;
            /* upload to s3 */
            s3Client.putObject(new PutObjectRequest(s3Bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            /* confirm temp file is deleted */
            LOGGER.info(file.delete() ? "TEMP FILE DELETED" : "TEMP FILE NOT DELETED");
            /* update user avatar url */
            user.setAvatar(fileUrl);
            userDao.save(user);
            /* create json object for user feedback */
            jsonObject.put("status", "success");
            jsonObject.put("imageUrl", fileUrl);
            jsonObject.put("message", "File Uploaded Successfully.");
            LOGGER.info("SUCCESS");
        } catch (IOException | JSONException e) {
            LOGGER.info("SOMETHING WENT WRONG");
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    @Async
    public String uploadRecipeImage(MultipartFile multipartFile, Recipe recipe) {
        String fileUrl = "";
        JSONObject jsonObject = new JSONObject();
        try {
            /* get converted file to upload */
            File file = convertMultiPartToFile(multipartFile);
            /* set timestamp for unique filename */
            long timeStamp = new Date().getTime();
            /* set the filepath to save file */
            String filepath = "recipes/"+recipe.getId()+"/images/";
            /* add filepath to filename to map directory in s3 */
            String fileName =
                    filepath +
                    timeStamp + "-" +
                    multipartFile.getOriginalFilename().replace(" ", "_");
            /* create image url for db */
            fileUrl = s3Endpoint+"/"+s3Bucket+"/"+fileName;
            /* upload to s3 */
            s3Client.putObject(new PutObjectRequest(s3Bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            /* confirm temp file is deleted */
            LOGGER.info(file.delete() ? "TEMP FILE DELETED" : "TEMP FILE NOT DELETED");
            /*  create image for recipe */
            imageDao.save(new Image(fileUrl,recipe));
            /* create json object for user feedback */
            jsonObject.put("status", "success");
            jsonObject.put("imageUrl", fileUrl);
            jsonObject.put("message", "File Uploaded Successfully.");
            LOGGER.info("SUCCESS");
        } catch (IOException | JSONException e) {
            LOGGER.info("SOMETHING WENT WRONG");
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    // @Async annotation ensures that the method is executed in a different background thread
    // but not consume the main thread.
    @Async
    public void deleteFile(String keyName) {
        // get keyName from from the bucket url
        keyName = keyName.split("codeup-s3/")[1];
        LOGGER.info("Deleting file with name= " + keyName);
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3Bucket, keyName);
        s3Client.deleteObject(deleteObjectRequest);
        LOGGER.info("File deleted successfully.");
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";
        JSONObject jsonObject = new JSONObject();
        try {
            /* get converted file to upload */
            File file = convertMultiPartToFile(multipartFile);
            /* set timestamp for unique filename */
            long timeStamp = new Date().getTime();
            /* set the filepath to save file */
            String filepath = "resources/";
            /* add filepath to filename to map directory in cloud */
            String fileName =
                    filepath + timeStamp + "-" +
                            multipartFile.getOriginalFilename().replace(" ", "_");
            /* create image url for db */
            fileUrl = s3Endpoint+"/"+s3Bucket+"/"+fileName;
            /* upload to s3 */
            s3Client.putObject(new PutObjectRequest(s3Bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            /* confirm temp file is deleted */
            LOGGER.info(file.delete() ? "TEMP FILE DELETED" : "TEMP FILE NOT DELETED");
            /* create json object for user feedback */
            jsonObject.put("status", "success");
            jsonObject.put("imageUrl", fileUrl);
            jsonObject.put("message", "File Uploaded Successfully.");
            LOGGER.info("SUCCESS");
        } catch (IOException | JSONException e) {
            LOGGER.info("SOMETHING WENT WRONG");
            e.printStackTrace();
        }
        return null;
    }

    private File convertMultiPartToFile(MultipartFile multiPartFile) throws IOException {
        File file = new File(multiPartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multiPartFile.getBytes());
        fos.close();
        return file;
    }

}
