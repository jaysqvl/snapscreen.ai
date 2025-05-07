Resume Parser Playground
This playground showcases the OpenResume resume parser and its ability to parse information from a resume PDF. Click around the PDF examples below to observe different parsing results.

Resume Example 1
Borrowed from University of La Verne Career Center - Link

Resume Example 2
Created with OpenResume resume builder - Link

You can also add your resume below to access how well your resume would be parsed by similar Application Tracking Systems (ATS) used in job applications. The more information it can parse out, the better it indicates the resume is well formatted and easy to read. It is beneficial to have the name and email accurately parsed at the very least.

Browse a pdf file or drop it here

File data is used locally and never leaves your browser

Browse fileNo file chosen
Resume Parsing Results
Profile
Name	Leo Leopard
Email	lleopard@laverne.edu
Phone	(909) 555-5555
Location	La Verne, CA
Link	
Summary	To obtain an on-campus position serving my fellow students which utilizes my strong communication skills.
Education
School	University of La Verne, La Verne, CA
Degree	Bachelor of Arts, Business Administration
GPA	3.5
Date	Expected Graduation: June 2016
Descriptions	
Work Experience
Company	LionLike MindState, Pomona, CA
Job Title	Volunteer
Date	June 2012–Present
Descriptions	• Plan two yearly outreach events to highlight community members’ creativity in spoken word, poetry, music, and art
Company	YMCA, Pomona, CA
Job Title	Volunteer Swim Coach
Date	Summer 2013, 2014
Descriptions	• Instructed classes of up to 15 children on basic swimming skills
• Communicated regularly with parents on children’s progress
Skills
Descriptions	• Computer: Proficient in Windows and Mac OS, Microsoft Word, PowerPoint, and Excel
• Language: Fluent in Spanish
• Social Media: Facebook, Twitter, Instagram
Resume Parser Algorithm Deep Dive
For the technical curious, this section will dive into the OpenResume parser algorithm and walks through the 4 steps on how it works. (Note that the algorithm is designed to parse single column resume in English language)

Step 1. Read the text items from a PDF file
A PDF file is a standardized file format defined by the ISO 32000 specification. When you open up a PDF file using a text editor, you'll notice that the raw content looks encoded and is difficult to read. To display it in a readable format, you would need a PDF reader to decode and view the file. Similarly, the resume parser first needs to decode the PDF file in order to extract its text content.

While it is possible to write a custom PDF reader function following the ISO 32000 specification, it is much simpler to leverage an existing library. In this case, the resume parser uses Mozilla's open source pdf.js library to first extract all the text items in the file.

The table below lists 107 text items that are extracted from the resume PDF added. A text item contains the text content and also some metadata about the content, e.g. its x, y positions in the document, whether the font is bolded, or whether it starts a new line. (Note that x,y position is relative to the bottom left corner of the page, which is the origin 0,0)

#	Text Content	Metadata
1	2	X₁=569 X₂=576 Y=745
2		X=266 Y=729 Bold NewLine
3	Leo Leopard	X₁=266 X₂=346 Y=729 Bold
4		X=224 Y=714 NewLine
5	555 La Verne Way,	X₁=224 X₂=320 Y=714
6	La Verne, CA	X₁=323 X₂=388 Y=714
7		X=249 Y=700 NewLine
8	lleopard	X₁=249 X₂=292 Y=700
9	@laverne.edu	X₁=292 X₂=363 Y=700
10		X=265 Y=686 NewLine
11	(909) 555	X₁=265 X₂=317 Y=686
12	-	X₁=317 X₂=321 Y=686
13	5555	X₁=321 X₂=347 Y=686
14		X=36 Y=658 Bold NewLine
15	OBJECTIVE	X₁=36 X₂=96 Y=658 Bold
16		X=36 Y=644 NewLine
17	To obtain an on	X₁=36 X₂=116 Y=644
18	-	X₁=116 X₂=120 Y=644
19	campus position serving my fellow students which utilizes my strong communication	X₁=120 X₂=555 Y=644
20		X=36 Y=630 NewLine
21	skills.	X₁=36 X₂=65 Y=630
22		X=36 Y=602 Bold NewLine
23	EDUCATION	X₁=36 X₂=103 Y=602 Bold
24		X=36 Y=588 Bold NewLine
25	University of La Verne, La Verne, CA	X₁=36 X₂=233 Y=588 Bold
26	Expected Graduation: June 2016	X₁=410 X₂=576 Y=588
27		X=36 Y=574 NewLine
28	Bachelor of Arts,	X₁=36 X₂=121 Y=574
29	Business Administration	X₁=124 X₂=250 Y=574
30		X=36 Y=559 NewLine
31	GPA: 3.5	X₁=36 X₂=79 Y=559
32		X=36 Y=532 Bold NewLine
33	HONORS/AWARDS	X₁=36 X₂=139 Y=532 Bold
34		X=36 Y=518 Bold NewLine
35	Dean’s List	X₁=36 X₂=96 Y=518 Bold
36	Fall 2013	X₁=453 X₂=501 Y=518
37	–	X₁=504 X₂=510 Y=518
38	Spring 2014	X₁=512 X₂=575 Y=518
39		X=36 Y=490 Bold NewLine
40	ON CAMPUS INVOLVEMENT	X₁=36 X₂=187 Y=490 Bold
41		X=36 Y=476 Bold NewLine
42	Enactus, University of La Verne	X₁=36 X₂=207 Y=476 Bold
43	August 2013	X₁=458 X₂=524 Y=476
44	–	X₁=526 X₂=532 Y=476
45	Present	X₁=535 X₂=574 Y=476
46		X=36 Y=462 NewLine
47	Member	X₁=36 X₂=77 Y=462
48		X=54 Y=447 NewLine
49	•	X₁=54 X₂=60 Y=447
50	Implement collective ideas to sponsor campus and community events which promote educational	X₁=72 X₂=571 Y=447
51		X=72 Y=433 NewLine
52	and social change	X₁=72 X₂=162 Y=433
53		X=54 Y=418 NewLine
54	•	X₁=54 X₂=60 Y=418
55	Led groups of 9 junior high	X₁=72 X₂=210 Y=418
56	students in discussion on success skills, business ethics, and personal	X₁=213 X₂=567 Y=418
57		X=72 Y=404 NewLine
58	finances	X₁=72 X₂=115 Y=404
59		X=54 Y=389 NewLine
60	•	X₁=54 X₂=60 Y=389
61	Co	X₁=72 X₂=85 Y=389
62	-	X₁=85 X₂=89 Y=389
63	designed 17 minute audio	X₁=89 X₂=222 Y=389
64	-	X₁=222 X₂=226 Y=389
65	visual presentation accurately and creatively describing project for	X₁=226 X₂=568 Y=389
66		X=72 Y=375 NewLine
67	use in regional and national competition	X₁=72 X₂=279 Y=375
68		X=36 Y=347 Bold NewLine
69	VOLUNTEER EXPERIENCE	X₁=36 X₂=177 Y=347 Bold
70		X=36 Y=333 Bold NewLine
71	LionL	X₁=36 X₂=67 Y=333 Bold
72	ike MindSt	X₁=67 X₂=126 Y=333 Bold
73	ate, Pomona, CA	X₁=126 X₂=214 Y=333 Bold
74	June 2012	X₁=472 X₂=524 Y=333
75	–	X₁=526 X₂=532 Y=333
76	Present	X₁=535 X₂=574 Y=333
77		X=36 Y=319 NewLine
78	Volunteer	X₁=36 X₂=85 Y=319
79		X=54 Y=304 NewLine
80	•	X₁=54 X₂=60 Y=304
81	Plan two yearly outreach events to highlight community members’ creativity in spoken word,	X₁=72 X₂=549 Y=304
82		X=72 Y=290 NewLine
83	poetry, music, and art	X₁=72 X₂=183 Y=290
84		X=36 Y=262 Bold NewLine
85	YMCA, Pomona, CA	X₁=36 X₂=139 Y=262 Bold
86	Summer 2013, 2014	X₁=471 X₂=575 Y=262
87		X=36 Y=248 NewLine
88	Vo	X₁=36 X₂=49 Y=248
89	lunteer Swim Coach	X₁=49 X₂=148 Y=248
90		X=54 Y=233 NewLine
91	•	X₁=54 X₂=60 Y=233
92	Instructed classes of up to 15 children on basic swimming skills	X₁=72 X₂=397 Y=233
93		X=54 Y=218 NewLine
94	•	X₁=54 X₂=60 Y=218
95	Communicated regularly with parents on children’s progress	X₁=72 X₂=383 Y=218
96		X=36 Y=190 Bold NewLine
97	SKILLS	X₁=36 X₂=74 Y=190 Bold
98		X=36 Y=176 Bold NewLine
99	Computer:	X₁=36 X₂=94 Y=176 Bold
100	Proficient in Windows and Mac OS, Microsoft Word, PowerPoint, and Excel	X₁=97 X₂=479 Y=176
101		X=36 Y=162 Bold NewLine
102	Language:	X₁=36 X₂=92 Y=162 Bold
103	Fluent in Spa	X₁=95 X₂=161 Y=162
104	nish	X₁=161 X₂=183 Y=162
105		X=36 Y=148 Bold NewLine
106	Social Media:	X₁=36 X₂=108 Y=148 Bold
107	Facebook, Twitter, Instagram	X₁=111 X₂=261 Y=148
Step 2. Group text items into lines
The extracted text items aren't ready to use yet and have 2 main issues:

Issue 1: They have some unwanted noises.
Some single text items can get broken into multiple ones, as you might observe on the table above, e.g. a phone number "(123) 456-7890" might be broken into 3 text items "(123) 456", "-" and "7890".

Solution: To tackle this issue, the resume parser connects adjacent text items into one text item if their distance is smaller than the average typical character width, where
Distance
=
RightTextItemX₁
-
LeftTextItemX₂
The average typical character width is calculated by dividing the sum of all text items' widths by the total number characters of the text items (Bolded texts and new line elements are excluded to not skew the results).

Issue 2: They lack contexts and associations.
When we read a resume, we scan a resume line by line. Our brains can process each section via visual cues such as texts' boldness and proximity, where we can quickly associate texts closer together to be a related group. The extracted text items however currently don't have those contexts/associations and are just disjointed elements.

Solution: To tackle this issue, the resume parser reconstructs those contexts and associations similar to how our brain would read and process the resume. It first groups text items into lines since we read text line by line. It then groups lines into sections, which will be discussed in the next step.

At the end of step 2, the resume parser extracts 36 lines from the resume PDF added, as shown in the table below. The result is much more readable when displayed in lines. (Some lines might have multiple text items, which are separated by a blue vertical divider )

Lines	Line Content
1	2
2	Leo Leopard
3	555 La Verne Way, La Verne, CA
4	lleopard@laverne.edu
5	(909) 555-5555
6	OBJECTIVE
7	To obtain an on-campus position serving my fellow students which utilizes my strong communication
8	skills.
9	EDUCATION
10	University of La Verne, La Verne, CAExpected Graduation: June 2016
11	Bachelor of Arts, Business Administration
12	GPA: 3.5
13	HONORS/AWARDS
14	Dean’s ListFall 2013–Spring 2014
15	ON CAMPUS INVOLVEMENT
16	Enactus, University of La VerneAugust 2013–Present
17	Member
18	•Implement collective ideas to sponsor campus and community events which promote educational
19	and social change
20	•Led groups of 9 junior highstudents in discussion on success skills, business ethics, and personal
21	finances
22	•Co-designed 17 minute audio-visual presentation accurately and creatively describing project for
23	use in regional and national competition
24	VOLUNTEER EXPERIENCE
25	LionLike MindState, Pomona, CAJune 2012–Present
26	Volunteer
27	•Plan two yearly outreach events to highlight community members’ creativity in spoken word,
28	poetry, music, and art
29	YMCA, Pomona, CASummer 2013, 2014
30	Volunteer Swim Coach
31	•Instructed classes of up to 15 children on basic swimming skills
32	•Communicated regularly with parents on children’s progress
33	SKILLS
34	Computer: Proficient in Windows and Mac OS, Microsoft Word, PowerPoint, and Excel
35	Language: Fluent in Spanish
36	Social Media: Facebook, Twitter, Instagram
Step 3. Group lines into sections
At step 2, the resume parser starts building contexts and associations to text items by first grouping them into lines. Step 3 continues the process to build additional associations by grouping lines into sections.

Note that every section (except the profile section) starts with a section title that takes up the entire line. This is a common pattern not just in resumes but also in books and blogs. The resume parser uses this pattern to group lines into the closest section title above these lines.

The resume parser applies some heuristics to detect a section title. The main heuristic to determine a section title is to check if it fulfills all 3 following conditions:
1. It is the only text item in the line
2. It is bolded
3. Its letters are all UPPERCASE

In simple words, if a text item is double emphasized to be both bolded and uppercase, it is most likely a section title in a resume. This is generally true for a well formatted resume. There can be exceptions, but it is likely not a good use of bolded and uppercase in those cases.

The resume parser also has a fallback heuristic if the main heuristic doesn't apply. The fallback heuristic mainly performs a keyword matching against a list of common resume section title keywords.

At the end of step 3, the resume parser identifies the sections from the resume and groups those lines with the associated section title, as shown in the table below. Note that the section titles are bolded and the lines associated with the section are highlighted with the same colors.

Lines	Line Content
PROFILE
1	2
2	Leo Leopard
3	555 La Verne Way, La Verne, CA
4	lleopard@laverne.edu
5	(909) 555-5555
6	OBJECTIVE
7	To obtain an on-campus position serving my fellow students which utilizes my strong communication
8	skills.
9	EDUCATION
10	University of La Verne, La Verne, CAExpected Graduation: June 2016
11	Bachelor of Arts, Business Administration
12	GPA: 3.5
13	HONORS/AWARDS
14	Dean’s ListFall 2013–Spring 2014
15	ON CAMPUS INVOLVEMENT
16	Enactus, University of La VerneAugust 2013–Present
17	Member
18	•Implement collective ideas to sponsor campus and community events which promote educational
19	and social change
20	•Led groups of 9 junior highstudents in discussion on success skills, business ethics, and personal
21	finances
22	•Co-designed 17 minute audio-visual presentation accurately and creatively describing project for
23	use in regional and national competition
24	VOLUNTEER EXPERIENCE
25	LionLike MindState, Pomona, CAJune 2012–Present
26	Volunteer
27	•Plan two yearly outreach events to highlight community members’ creativity in spoken word,
28	poetry, music, and art
29	YMCA, Pomona, CASummer 2013, 2014
30	Volunteer Swim Coach
31	•Instructed classes of up to 15 children on basic swimming skills
32	•Communicated regularly with parents on children’s progress
33	SKILLS
34	Computer: Proficient in Windows and Mac OS, Microsoft Word, PowerPoint, and Excel
35	Language: Fluent in Spanish
36	Social Media: Facebook, Twitter, Instagram
Step 4. Extract resume from sections
Step 4 is the last step of the resume parsing process and is also the core of the resume parser, where it extracts resume information from the sections.

Feature Scoring System
The gist of the extraction engine is a feature scoring system. Each resume attribute to be extracted has a custom feature sets, where each feature set consists of a feature matching function and a feature matching score if matched (feature matching score can be a positive or negative number). To compute the final feature score of a text item for a particular resume attribute, it would run the text item through all its feature sets and sum up the matching feature scores. This process is carried out for all text items within the section, and the text item with the highest computed feature score is identified as the extracted resume attribute.

As a demonstration, the table below shows 3 resume attributes in the profile section of the resume PDF added.

Resume Attribute	Text (Highest Feature Score)	Feature Scores of Other Texts
Name	Leo Leopard	5 Leo Leopard
-4 2
-4 lleopard@laverne.edu
-8 (909) 555-5555
-10 555 La Verne Way, La Verne, CA
Email	lleopard@laverne.edu	4 lleopard@laverne.edu
0 2
-1 Leo Leopard
-4 (909) 555-5555
-8 555 La Verne Way, La Verne, CA
Phone	(909) 555-5555	4 (909) 555-5555
0 2
-4 Leo Leopard
-4 555 La Verne Way, La Verne, CA
-4 lleopard@laverne.edu
In the resume PDF added, the resume attribute name is likely to be "Leo Leopard" since its feature score is 5, which is the highest feature score out of all text items in the profile section. (Some text items' feature scores can be negative, indicating they are very unlikely to be the targeted attribute)

Feature Sets
Having explained the feature scoring system, we can dive more into how feature sets are constructed for a resume attribute. It follows 2 principles:
1. A resume attribute's feature sets are designed relative to all other resume attributes within the same section.
2. A resume attribute's feature sets are manually crafted based on its characteristics and likelihood of each characteristic.

The table below lists some of the feature sets for the resume attribute name. It contains feature function that matches the name attribute with positive feature score and also feature function that only matches other resume attributes in the section with negative feature score.

Name Feature Sets
Feature Function	Feature Matching Score
Contains only letters, spaces or periods	+3
Is bolded	+2
Contains all uppercase letters	+2
Contains @	-4 (match email)
Contains number	-4 (match phone)
Contains ,	-4 (match address)
Contains /	-4 (match url)
Core Feature Function
Each resume attribute has multiple feature sets. They can be found in the source code under the extract-resume-from-sections folder and we won't list them all out here. Each resume attribute usually has a core feature function that greatly identifies them, so we will list out the core feature function below.

Resume Attribute	Core Feature Function	Regex
Name	Contains only letters, spaces or periods	/^[a-zA-Z\s\.]+$/
Email	Match email format xxx@xxx.xxx
xxx can be anything not space	/\S+@\S+\.\S+/
Phone	Match phone format (xxx)-xxx-xxxx
() and - are optional	/\(?\d{3}\)?[\s-]?\d{3}[\s-]?\d{4}/
Location	Match city and state format City, ST	/[A-Z][a-zA-Z\s]+, [A-Z]{2}/
Url	Match url format xxx.xxx/xxx	/\S+\.[a-z]+\/\S+/
School	Contains a school keyword, e.g. College, University, School	
Degree	Contains a degree keyword, e.g. Associate, Bachelor, Master	
GPA	Match GPA format x.xx	/[0-4]\.\d{1,2}/
Date	Contains date keyword related to year, month, seasons or the word Present	Year: /(?:19|20)\d{2}/
Job Title	Contains a job title keyword, e.g. Analyst, Engineer, Intern	
Company	Is bolded or doesn't match job title & date	
Project	Is bolded or doesn't match date	
Special Case: Subsections
The last thing that is worth mentioning is subsections. For profile section, we can directly pass all the text items to the feature scoring systems. But for other sections, such as education and work experience, we have to first divide the section into subsections since there can be multiple schools or work experiences in the section. The feature scoring system then process each subsection to retrieve each's resume attributes and append the results.

The resume parser applies some heuristics to detect a subsection. The main heuristic to determine a subsection is to check if the vertical line gap between 2 lines is larger than the typical line gap * 1.4, since a well formatted resume usually creates a new empty line break before adding the next subsection. There is also a fallback heuristic if the main heuristic doesn't apply to check if the text item is bolded.

And that is everything about the OpenResume parser algorithm :)