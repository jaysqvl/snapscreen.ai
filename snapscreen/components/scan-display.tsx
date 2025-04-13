"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion"
import { Skeleton } from "@/components/ui/skeleton"

// Types
type CheckStatus = "pass" | "fail" | "needs-attention"

type ScanCheck = {
  id: string;
  name: string;
  status: CheckStatus;
  feedback: string;
}

type SkillMatch = {
  name: string;
  resumeCount: number;
  jobDescriptionCount: number;
}

type CheckCategory = {
  id: string;
  name: string;
  checks: ScanCheck[];
}

type ScanData = {
  id: string;
  title: string;
  fileName: string;
  dateScanned: string;
  score: number;
  passingChecks: number;
  totalChecks: number;
  categories: CheckCategory[];
  hardSkills: SkillMatch[];
  softSkills: SkillMatch[];
}

// Mock data for development
const MOCK_SCAN: ScanData = {
  id: "1",
  title: "Software Engineer Resume",
  fileName: "john_doe_resume.pdf",
  dateScanned: "2023-10-15",
  score: 82,
  passingChecks: 16,
  totalChecks: 21,
  categories: [
    {
      id: "searchability",
      name: "Searchability",
      checks: [
        {
          id: "contact-email",
          name: "Email",
          status: "pass",
          feedback: "You provided your email. Recruiters use your email to contact you for job matches."
        },
        {
          id: "contact-phone",
          name: "Phone Number",
          status: "fail",
          feedback: "We did not find a phone number in your resume. Some recruiters prefer a phone call to email."
        },
        {
          id: "contact-address",
          name: "Address",
          status: "fail",
          feedback: "We did not find an address in your resume. Recruiters use your address to validate your location for job matches."
        },
        {
          id: "summary",
          name: "Summary",
          status: "fail",
          feedback: "We did not find a summary section on your resume. The summary provides a quick overview of the candidate's qualifications."
        },
        {
          id: "section-education",
          name: "Education Section",
          status: "pass",
          feedback: "We found the education section in your resume."
        },
        {
          id: "section-experience",
          name: "Work Experience Section",
          status: "pass",
          feedback: "We found the work experience section in your resume."
        },
        {
          id: "job-title-match",
          name: "Job Title Match",
          status: "pass",
          feedback: "Your resume includes the exact job title from the job description."
        },
        {
          id: "date-formatting",
          name: "Date Formatting",
          status: "pass",
          feedback: "The dates in your work experience section are properly formatted."
        },
        {
          id: "education-match",
          name: "Education Match",
          status: "pass",
          feedback: "Your education matches the requirements in the job description."
        },
        {
          id: "file-type",
          name: "File Type",
          status: "pass",
          feedback: "You are using a PDF resume, which is the preferred format for most ATS systems."
        },
        {
          id: "file-name",
          name: "File Name",
          status: "pass",
          feedback: "Your file name is concise and readable without special characters."
        }
      ]
    },
    {
      id: "formatting",
      name: "Formatting",
      checks: [
        {
          id: "margins",
          name: "Margins",
          status: "needs-attention",
          feedback: "Margins may be too narrow for optimal ATS parsing."
        },
        {
          id: "font",
          name: "Font",
          status: "pass",
          feedback: "You're using a standard font that's ATS-friendly."
        },
        {
          id: "length",
          name: "Resume Length",
          status: "pass",
          feedback: "Resume length is appropriate (1-2 pages)."
        },
        {
          id: "bullets",
          name: "Bullet Points",
          status: "pass",
          feedback: "Your bullet points are formatted consistently."
        }
      ]
    },
    {
      id: "recruiter-tips",
      name: "Recruiter Tips",
      checks: [
        {
          id: "quantifiable-results",
          name: "Quantifiable Results",
          status: "needs-attention",
          feedback: "Consider adding more measurable achievements to your experience section."
        },
        {
          id: "action-verbs",
          name: "Action Verbs",
          status: "pass",
          feedback: "Good use of action verbs throughout your resume."
        }
      ]
    }
  ],
  hardSkills: [
    { name: "JavaScript", resumeCount: 3, jobDescriptionCount: 2 },
    { name: "React", resumeCount: 2, jobDescriptionCount: 3 },
    { name: "TypeScript", resumeCount: 1, jobDescriptionCount: 2 },
    { name: "Docker", resumeCount: 0, jobDescriptionCount: 2 },
    { name: "Kubernetes", resumeCount: 0, jobDescriptionCount: 1 },
    { name: "Node.js", resumeCount: 2, jobDescriptionCount: 1 },
    { name: "AWS", resumeCount: 1, jobDescriptionCount: 2 }
  ],
  softSkills: [
    { name: "Communication", resumeCount: 1, jobDescriptionCount: 2 },
    { name: "Teamwork", resumeCount: 2, jobDescriptionCount: 1 },
    { name: "Problem Solving", resumeCount: 1, jobDescriptionCount: 2 },
    { name: "Leadership", resumeCount: 0, jobDescriptionCount: 1 },
    { name: "Time Management", resumeCount: 1, jobDescriptionCount: 1 }
  ]
}

interface ScanDisplayProps {
  scanId: string | null;
}

export function ScanDisplay({ scanId }: ScanDisplayProps) {
  const [loading, setLoading] = useState(true)
  const [scanData, setScanData] = useState<ScanData | null>(null)

  useEffect(() => {
    // In a real application, this would fetch data from an API
    if (scanId) {
      // Get mock data immediately for development
      setScanData(MOCK_SCAN)
      setLoading(false)
    } else {
      setScanData(null)
      setLoading(false)
    }
  }, [scanId])

  if (!scanId) {
    return (
      <div className="flex h-full items-center justify-center">
        <Card className="w-[450px]">
          <CardHeader>
            <CardTitle>Welcome to Resume Scanner</CardTitle>
            <CardDescription>
              Select a scan from the sidebar or create a new scan to get started.
            </CardDescription>
          </CardHeader>
        </Card>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="p-6 space-y-6">
        <Skeleton className="h-12 w-64" />
        <div className="space-y-2">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
        </div>
        <div className="space-y-2">
          <Skeleton className="h-10 w-full rounded-lg" />
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
            <Skeleton className="h-48 w-full rounded-lg" />
            <Skeleton className="h-48 w-full rounded-lg" />
          </div>
        </div>
      </div>
    )
  }
  
  // Helper function to count checks by status in a category
  const countChecksByStatus = (category: CheckCategory, status: CheckStatus): number => {
    return category.checks.filter(check => check.status === status).length;
  }

  return (
    <div className="p-6 space-y-6">
      <Card className="mb-6">
        <CardHeader>
          <CardTitle>{scanData?.title}</CardTitle>
          <CardDescription>
            Scanned on {scanData?.dateScanned} • {scanData?.fileName}
          </CardDescription>
        </CardHeader>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Resume Match Score</CardTitle>
          <CardDescription>
            How well your resume matches the job requirements
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            <div>
              <div className="flex justify-between mb-2">
                <span className="text-sm font-medium">Match Percentage</span>
                <span className="text-sm font-medium">{scanData?.score}%</span>
              </div>
              <Progress value={scanData?.score} className="h-2" />
            </div>

            <div className="space-y-2">
              <div className="flex justify-between">
                <span>Overall Checks</span>
                <span>{scanData?.passingChecks} of {scanData?.totalChecks} passing</span>
              </div>
              <div className="flex flex-wrap gap-2">
                <Badge variant="outline" className="bg-green-50 text-green-700 hover:bg-green-100 hover:text-green-800">
                  {scanData?.categories?.flatMap(c => c.checks).filter(c => c.status === "pass").length || 0} Passed
                </Badge>
                <Badge variant="outline" className="bg-red-50 text-red-700 hover:bg-red-100 hover:text-red-800">
                  {scanData?.categories?.flatMap(c => c.checks).filter(c => c.status === "fail").length || 0} Failed
                </Badge>
                <Badge variant="outline" className="bg-yellow-50 text-yellow-700 hover:bg-yellow-100 hover:text-yellow-800">
                  {scanData?.categories?.flatMap(c => c.checks).filter(c => c.status === "needs-attention").length || 0} Needs Attention
                </Badge>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <Accordion type="multiple" className="w-full space-y-4">
        {scanData?.categories.map(category => (
          <Card key={category.id}>
            <AccordionItem value={category.id} className="border-none">
              <AccordionTrigger className="px-6 py-4 hover:no-underline">
                <div className="flex items-center justify-between w-full">
                  <div>
                    <h3 className="text-xl font-semibold">{category.name}</h3>
                    <p className="text-sm text-muted-foreground">
                      {countChecksByStatus(category, "pass")} of {category.checks.length} checks passing
                    </p>
                  </div>
                  <div className="flex gap-2">
                    {countChecksByStatus(category, "pass") > 0 && (
                      <Badge variant="outline" className="bg-green-50 text-green-700">
                        {countChecksByStatus(category, "pass")} Passed
                      </Badge>
                    )}
                    {countChecksByStatus(category, "fail") > 0 && (
                      <Badge variant="outline" className="bg-red-50 text-red-700">
                        {countChecksByStatus(category, "fail")} Failed
                      </Badge>
                    )}
                    {countChecksByStatus(category, "needs-attention") > 0 && (
                      <Badge variant="outline" className="bg-yellow-50 text-yellow-700">
                        {countChecksByStatus(category, "needs-attention")} Needs Attention
                      </Badge>
                    )}
                  </div>
                </div>
              </AccordionTrigger>
              <AccordionContent className="pb-4 px-6">
                <div className="space-y-4">
                  {category.checks.map(check => (
                    <div key={check.id} className="py-2 border-b last:border-b-0">
                      <div className="flex items-center justify-between mb-1">
                        <h4 className="font-medium">{check.name}</h4>
                        <Badge className={
                          check.status === "pass" ? "bg-green-100 text-green-800" :
                          check.status === "fail" ? "bg-red-100 text-red-800" :
                          "bg-yellow-100 text-yellow-800"
                        }>
                          {check.status === "pass" ? "Pass" : 
                           check.status === "fail" ? "Fail" : 
                           "Needs Attention"}
                        </Badge>
                      </div>
                      <p className="text-sm text-gray-600">{check.feedback}</p>
                    </div>
                  ))}
                </div>
              </AccordionContent>
            </AccordionItem>
          </Card>
        ))}
      
        <Card>
          <AccordionItem value="hard-skills" className="border-none">
            <AccordionTrigger className="px-6 py-4 hover:no-underline">
              <div className="flex items-center justify-between w-full">
                <div>
                  <h3 className="text-xl font-semibold">Hard Skills</h3>
                  <p className="text-sm text-muted-foreground">
                    {scanData?.hardSkills.filter(s => s.resumeCount > 0).length} of {scanData?.hardSkills.length} skills found in your resume
                  </p>
                </div>
                <div className="flex gap-2">
                  {scanData?.hardSkills.filter(s => s.resumeCount > 0).length && (
                    <Badge variant="outline" className="bg-green-50 text-green-700">
                      {scanData?.hardSkills.filter(s => s.resumeCount > 0).length} Found
                    </Badge>
                  )}
                  {scanData?.hardSkills.filter(s => s.resumeCount === 0 && s.jobDescriptionCount > 0).length ? (
                    <Badge variant="outline" className="bg-red-50 text-red-700">
                      {scanData?.hardSkills.filter(s => s.resumeCount === 0 && s.jobDescriptionCount > 0).length} Missing
                    </Badge>
                  ) : null}
                </div>
              </div>
            </AccordionTrigger>
            <AccordionContent className="pb-4 px-6">
              <div className="border rounded-md">
                <table className="w-full">
                  <thead>
                    <tr className="border-b bg-muted/50">
                      <th className="text-left p-2 pl-4">Skill</th>
                      <th className="text-center p-2">In Resume</th>
                      <th className="text-center p-2">In Job Description</th>
                      <th className="text-center p-2 pr-4">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {scanData?.hardSkills.map((skill, index) => (
                      <tr key={skill.name} className={index < scanData.hardSkills.length - 1 ? "border-b" : ""}>
                        <td className="p-2 pl-4 font-medium">{skill.name}</td>
                        <td className="text-center p-2">{skill.resumeCount}</td>
                        <td className="text-center p-2">{skill.jobDescriptionCount}</td>
                        <td className="text-center p-2 pr-4">
                          {skill.resumeCount > 0 ? (
                            <Badge className="bg-green-100 text-green-800">Found</Badge>
                          ) : (
                            <Badge className="bg-red-100 text-red-800">Missing</Badge>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Card>
        
        <Card>
          <AccordionItem value="soft-skills" className="border-none">
            <AccordionTrigger className="px-6 py-4 hover:no-underline">
              <div className="flex items-center justify-between w-full">
                <div>
                  <h3 className="text-xl font-semibold">Soft Skills</h3>
                  <p className="text-sm text-muted-foreground">
                    {scanData?.softSkills.filter(s => s.resumeCount > 0).length} of {scanData?.softSkills.length} skills found in your resume
                  </p>
                </div>
                <div className="flex gap-2">
                  {scanData?.softSkills.filter(s => s.resumeCount > 0).length && (
                    <Badge variant="outline" className="bg-green-50 text-green-700">
                      {scanData?.softSkills.filter(s => s.resumeCount > 0).length} Found
                    </Badge>
                  )}
                  {scanData?.softSkills.filter(s => s.resumeCount === 0 && s.jobDescriptionCount > 0).length ? (
                    <Badge variant="outline" className="bg-red-50 text-red-700">
                      {scanData?.softSkills.filter(s => s.resumeCount === 0 && s.jobDescriptionCount > 0).length} Missing
                    </Badge>
                  ) : null}
                </div>
              </div>
            </AccordionTrigger>
            <AccordionContent className="pb-4 px-6">
              <div className="border rounded-md">
                <table className="w-full">
                  <thead>
                    <tr className="border-b bg-muted/50">
                      <th className="text-left p-2 pl-4">Skill</th>
                      <th className="text-center p-2">In Resume</th>
                      <th className="text-center p-2">In Job Description</th>
                      <th className="text-center p-2 pr-4">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {scanData?.softSkills.map((skill, index) => (
                      <tr key={skill.name} className={index < scanData.softSkills.length - 1 ? "border-b" : ""}>
                        <td className="p-2 pl-4 font-medium">{skill.name}</td>
                        <td className="text-center p-2">{skill.resumeCount}</td>
                        <td className="text-center p-2">{skill.jobDescriptionCount}</td>
                        <td className="text-center p-2 pr-4">
                          {skill.resumeCount > 0 ? (
                            <Badge className="bg-green-100 text-green-800">Found</Badge>
                          ) : (
                            <Badge className="bg-red-100 text-red-800">Missing</Badge>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </AccordionContent>
          </AccordionItem>
        </Card>
      </Accordion>
    </div>
  )
} 