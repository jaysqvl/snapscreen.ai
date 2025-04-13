import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AppNavbar } from "@/components/app-navbar";

export default function ProcessPage() {
  return (
    <div className="flex min-h-screen w-full flex-col">
      <AppNavbar />
      <div className="w-full">
        <div className="container mx-auto py-10">
          <div className="mb-8">
            <h1 className="text-4xl font-bold mb-2">Technical Process</h1>
            <p className="text-gray-500">
              Detailed documentation of how our resume screening platform works under the hood.
            </p>
          </div>

          <Tabs defaultValue="overview" className="w-full">
            <TabsList className="mb-4">
              <TabsTrigger value="overview">Overview</TabsTrigger>
              <TabsTrigger value="parser">Resume Parser</TabsTrigger>
              <TabsTrigger value="api">API Architecture</TabsTrigger>
              <TabsTrigger value="algorithms">Algorithms</TabsTrigger>
            </TabsList>

            <TabsContent value="overview">
              <Card>
                <CardHeader>
                  <CardTitle>Tech Stack Overview</CardTitle>
                  <CardDescription>The key technologies powering our platform</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div>
                      <h3 className="text-lg font-medium">Frontend</h3>
                      <ul className="list-disc pl-5 mt-2">
                        <li>React with TypeScript for type safety</li>
                        <li>Next.js for server-side rendering and routing</li>
                        <li>TailwindCSS for styling</li>
                        <li>ShadCN/UI for component library</li>
                      </ul>
                    </div>
                    <div>
                      <h3 className="text-lg font-medium">Backend</h3>
                      <ul className="list-disc pl-5 mt-2">
                        <li>Java Spring Boot for REST API</li>
                        <li>Spring Data JPA for database operations</li>
                        <li>Firebase Authentication</li>
                      </ul>
                    </div>
                    <div>
                      <h3 className="text-lg font-medium">Database &amp; Storage</h3>
                      <ul className="list-disc pl-5 mt-2">
                        <li>PostgreSQL for structured data</li>
                        <li>AWS S3 for resume file storage</li>
                      </ul>
                    </div>
                    <div>
                      <h3 className="text-lg font-medium">Deployment</h3>
                      <ul className="list-disc pl-5 mt-2">
                        <li>Docker for containerization</li>
                        <li>AWS ECS/EKS for orchestration</li>
                        <li>CI/CD with GitHub Actions</li>
                      </ul>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="parser">
              <Card>
                <CardHeader>
                  <CardTitle>Resume Parser</CardTitle>
                  <CardDescription>How we analyze and extract information from resumes</CardDescription>
                </CardHeader>
                <CardContent>
                  <Accordion type="single" collapsible className="w-full">
                    <AccordionItem value="item-1">
                      <AccordionTrigger>Document Processing Pipeline</AccordionTrigger>
                      <AccordionContent>
                        <p className="mb-4">
                          Our resume parser uses a multi-stage pipeline to process documents:
                        </p>
                        <ol className="list-decimal pl-5 space-y-2">
                          <li>Document conversion (PDF/DOCX to plain text)</li>
                          <li>Text preprocessing and normalization</li>
                          <li>Section identification and segmentation</li>
                          <li>Entity extraction (skills, education, experience)</li>
                          <li>Structured data generation</li>
                        </ol>
                      </AccordionContent>
                    </AccordionItem>
                    <AccordionItem value="item-2">
                      <AccordionTrigger>Text Extraction Techniques</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          We use a combination of regular expressions, NLP techniques, and 
                          machine learning to extract information accurately from various 
                          resume formats. Our parser can identify section headings, contact 
                          information, work experience, education details, and skills.
                        </p>
                      </AccordionContent>
                    </AccordionItem>
                    <AccordionItem value="item-3">
                      <AccordionTrigger>Supported File Formats</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          Currently supported file formats include PDF and DOCX. We use 
                          Apache PDFBox for PDF parsing and Apache POI for Microsoft 
                          Office document processing.
                        </p>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="api">
              <Card>
                <CardHeader>
                  <CardTitle>API Architecture</CardTitle>
                  <CardDescription>The design and structure of our backend services</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-6">
                    <div>
                      <h3 className="text-lg font-medium mb-2">REST API Endpoints</h3>
                      <p className="mb-2">Our API follows RESTful principles with these main endpoints:</p>
                      <ul className="list-disc pl-5 space-y-1">
                        <li><code>/api/resumes</code> - Resume upload and management</li>
                        <li><code>/api/job-descriptions</code> - Job description storage</li>
                        <li><code>/api/scans</code> - Resume scanning and analysis</li>
                        <li><code>/api/users</code> - User profile management</li>
                      </ul>
                    </div>
                    <div>
                      <h3 className="text-lg font-medium mb-2">Authentication</h3>
                      <p>
                        We use Firebase Authentication with JWT tokens for secure access to our API.
                        Each request to protected endpoints must include a valid token in the
                        Authorization header.
                      </p>
                    </div>
                    <div>
                      <h3 className="text-lg font-medium mb-2">File Processing</h3>
                      <p>
                        Files uploaded through our API are temporarily stored on the server,
                        processed for analysis, and then moved to permanent storage in AWS S3.
                        References to these files are stored in the PostgreSQL database.
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="algorithms">
              <Card>
                <CardHeader>
                  <CardTitle>Scoring Algorithms</CardTitle>
                  <CardDescription>How we determine resume-job description matches</CardDescription>
                </CardHeader>
                <CardContent>
                  <Accordion type="single" collapsible className="w-full">
                    <AccordionItem value="item-1">
                      <AccordionTrigger>Keyword Matching</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          We extract keywords and phrases from job descriptions using NLP
                          techniques including TF-IDF scoring and entity recognition. These
                          are then compared against the resume content to determine keyword
                          match percentage.
                        </p>
                      </AccordionContent>
                    </AccordionItem>
                    <AccordionItem value="item-2">
                      <AccordionTrigger>Section Analysis</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          Our algorithms check for the presence and quality of essential
                          resume sections (contact info, education, experience, skills).
                          Each section is scored individually and contributes to the overall
                          score.
                        </p>
                      </AccordionContent>
                    </AccordionItem>
                    <AccordionItem value="item-3">
                      <AccordionTrigger>Format Validation</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          We analyze formatting consistency, checking for proper date formats,
                          consistent section headings, appropriate use of bullet points, and
                          other structural elements that contribute to ATS-friendliness.
                        </p>
                      </AccordionContent>
                    </AccordionItem>
                    <AccordionItem value="item-4">
                      <AccordionTrigger>Composite Scoring</AccordionTrigger>
                      <AccordionContent>
                        <p>
                          The final score is calculated as a weighted average of individual
                          component scores. Weights are assigned based on the relative importance
                          of each factor in ATS systems:
                        </p>
                        <ul className="list-disc pl-5 mt-2">
                          <li>Keyword matching: 40%</li>
                          <li>Section completeness: 30%</li>
                          <li>Format compliance: 20%</li>
                          <li>Overall document quality: 10%</li>
                        </ul>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  );
} 