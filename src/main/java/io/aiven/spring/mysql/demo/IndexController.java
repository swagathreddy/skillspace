package io.aiven.spring.mysql.demo;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // Add this import statement
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;

import org.jsoup.nodes.Document; // Add this for Document
import org.jsoup.nodes.Element;
@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;


 

    @GetMapping("/about")
    public String about(){
        return "about";
    }

 

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/courses")
    public String courses(Model model){
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "courses";
    }
  

    @GetMapping("/")
    public String home(){
        
        return "index";
    }
    @GetMapping("/index")
    public String homee(){
        return "index";
    }
   
    @Autowired
    private RoadmapRepository roadmapRepository;

    @GetMapping("/roadmap")
    public String roadmap(Model model){
        List<Roadmap> roadmaps = roadmapRepository.findAll();
        model.addAttribute("roadmaps", roadmaps);
        return "roadmap";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());  // Always add a new User object
        return "register";
    }
    
    @PostMapping("/register")
public String registerUser(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String name,
                         @RequestParam String email,
                         Model model) {
    
    logger.info("Received registration request with username: {}, name: {}, email: {}", 
        username, name, email);
    
    try {
        // Create new user object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        
        // Check if username already exists
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        // Save the user
        userRepository.save(user);
        logger.info("Successfully registered user: {}", username);
        return "redirect:/login?registered=true";
        
    } catch (Exception e) {
        logger.error("Registration error", e);
        model.addAttribute("error", "Registration failed: " + e.getMessage());
        return "register";
    }
}



    
    @Autowired
private EntityManager entityManager;

@GetMapping("/test-db")
public String testDb() {
    try {
        entityManager.createNativeQuery("SELECT 1").getSingleResult();
        return "Database connection successful";
    } catch (Exception e) {
        return "Database connection failed: " + e.getMessage();
    }
}
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, 
                          @RequestParam String password, 
                          Model model,
                          HttpSession session) {  // Add HttpSession parameter
        // Check if the username and password match a user in the database
        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            // Store user in session
            session.setAttribute("user", user);
            return "redirect:/";  // Redirect to home page
        } else {
            // Display an error message if login fails
            model.addAttribute("error", "Incorrect username or password");
            return "login";
        }
    }

    // Add this method to handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Add this method to make user available to all templates
    @ModelAttribute
    public void addUserToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
    }

    

    @GetMapping("/discussion")
    public String discussion(Model model) {
        Iterable<Question> questions = questionRepository.findAll();
        Iterable<Answer> answers = answerRepository.findAll();

        model.addAttribute("questions", questions);
        model.addAttribute("answers", answers);

        return "discussion";
    }

    @GetMapping("/question/{id}")
    public String questionDetail(@PathVariable("id") Long questionId, Model model) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Invalid question id"));
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PostMapping("/add_answer/{questionId}")
    public String addAnswer(@PathVariable("questionId") Long questionId, @RequestParam("content") String content) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Invalid question id"));
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setContent(content);
        answerRepository.save(answer);
        return "redirect:/discussion";
    }

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    


    @PostMapping("/add_question")
    public String addQuestion(@RequestParam("title") String title) {
        Question question = new Question();
        question.setTitle(title);
        questionRepository.save(question);
        return "redirect:/discussion";
    }


    @GetMapping("/Rank")
    public String showRankPage() {
        return "Rank";
    }

    
       @GetMapping("/getProblemSolves")
    public ResponseEntity<?> getProblemSolves(@RequestParam String geeksUsername, @RequestParam String leetcodeUsername) {
        String geeksProblemSolves = getGeeksforGeeksProblemSolves(geeksUsername);
        String leetcodeProblemSolves = getLeetCodeProblemSolves(leetcodeUsername);
    
        Map<String, String> response = new HashMap<>();
        response.put("geeksProblemSolves", geeksProblemSolves);
        response.put("leetcodeProblemSolves", leetcodeProblemSolves);
    
        return ResponseEntity.ok(response);
    }

    private String getGeeksforGeeksProblemSolves(String username) {
    try {
        String url = "https://www.geeksforgeeks.org/user/" + username + "/";
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("Connection", "keep-alive")
            .timeout(30000)
            .get();
        
        // Use the exact class selector you provided
        Element scoreElement = doc.selectFirst(".scoreCard_head_left--score__oSi_x");
        if (scoreElement != null) {
            return scoreElement.text();
        }
        
        logger.info("Could not find score element for GFG user: {}", username);
        return "Profile not found";
    } catch (IOException e) {
        logger.error("Error fetching GeeksForGeeks profile: " + e.getMessage());
        return "Error retrieving profile";
    }
}

private String getLeetCodeProblemSolves(String username) {
    try {
        String url = "https://leetcode.com/u/" + username;
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("Cache-Control", "no-cache")
            .header("Pragma", "no-cache")
            .referrer("https://leetcode.com")
            .timeout(30000)
            .get();

        // Try multiple selector patterns
        Element solvesElement = null;
        
        // Try the new selector format
        solvesElement = doc.select("span[class*='text-[30px]'][class*='font-semibold'][class*='leading-[32px]']").first();
        
        // Fallback selectors
        if (solvesElement == null) {
            solvesElement = doc.select("div[data-cy='solved-count']").first();
        }
        if (solvesElement == null) {
            solvesElement = doc.select("div.css-1c0vxpt h2").first();
        }
        if (solvesElement == null) {
            solvesElement = doc.select("div.total-solved-count__2El1").first();
        }
        
        // Log the HTML for debugging
        logger.debug("LeetCode HTML for user {}: {}", username, doc.html());
        
        if (solvesElement != null) {
            return solvesElement.text().replaceAll("[^0-9]", ""); // Extract only numbers
        }
        
        logger.warn("Could not find solved problems count for LeetCode user: {}", username);
        return "Profile not found";
        
    } catch (IOException e) {
        logger.error("Error fetching LeetCode profile for user {}: {}", username, e.getMessage());
        return "Error retrieving profile";
    }
}
    }
    
    
    // Method to retrieve GeeksforGeeks rank
    
